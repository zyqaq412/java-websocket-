package com.hzy.webscoketserver.server;


import com.hzy.webscoketserver.enums.CodeEnum;
import com.hzy.webscoketserver.pojo.Message;
import com.hzy.webscoketserver.pojo.Notice;
import com.hzy.webscoketserver.pojo.User;
import com.hzy.webscoketserver.pojo.userMsg;
import com.hzy.webscoketserver.utils.JsonUtils;
import com.hzy.webscoketserver.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @title: ChatWebSocketHandler
 * @Author zxwyhzy
 * @Date: 2023/5/7 20:06
 * @Version 1.0
 *
 * WebSocketHandler 是一个接口，它定义了处理 WebSocket 通信的方法，
 * 实现该接口的类可以作为 WebSocket 处理器使用。WebSocketHandler 主要定义了三个方法：
 * handleTextMessage: 处理接收到的文本消息。
 * handleBinaryMessage: 处理接收到的二进制消息。
 * 这些方法都会接收一个 WebSocketSession 对象作为参数，它封装了 WebSocket 的连接会话，可以通过该对象发送消息、关闭连接等操作。
 * 除此之外，WebSocketHandler 还可以定义自己的握手拦截器，用于在连接建立前对请求进行验证或者设置一些参数等操作。
 */
@Slf4j
public class ChatWebSocketHandler implements WebSocketHandler {

    // 存放 sessionId 与 session
    private static Map<String,WebSocketSession> SESSIONS = new ConcurrentHashMap<>();
    // 在线用户列表
    private static List<User> ONLINE_USERS = new ArrayList<>();
    // 消息列表
    private static List <Message> msgList = new ArrayList<>();
    // 公告
    private static Notice notice = new Notice();

    /**
     *  WebSocket 连接建立后调用的方法，通常用于处理连接建立后的业务逻辑。
     * @param session
     * @throws Exception
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("WebSocket 连接已打开：" + session.getId());
        // 获取请求路径 判断是否携带用户名
        String uri = session.getUri().toString();
        // ws://localhost:8080/username=zxwy&id=1
        log.info(uri);
        // 获取id 与 用户名
        String id = uri.substring(uri.lastIndexOf('=')+1);
        uri = uri.substring(0,uri.lastIndexOf('&'));
        String username = uri.substring(uri.lastIndexOf('=')+1);

        if ("".equals(username)){
            session.sendMessage(new TextMessage(JsonUtils.toJson(Result.error(CodeEnum.NOT_USERNAME))));
            return;
        }
        User user = new User();
        user.setUsername(username);
        user.setId(Integer.valueOf(id));

        // 判断当前用户是否已经连接过
        List<User> onlineUser = ONLINE_USERS.stream()
                .filter(tmp -> tmp.getId()==user.getId())
                .collect(Collectors.toList());
        // 如果存在相同用户已经登录  删除之前登录的session并关闭
        if (onlineUser.size() != 0){
            delSessionById(onlineUser.get(0).getId());
        }

        SESSIONS.put(session.getId(),session);
        // 将用户添加到在线列表
        ONLINE_USERS.add(user);

        session.getAttributes().put(session.getId(),user);
        session.getAttributes().put("sessionId",session.getId());

        // 将连接id推送给前端
        session.sendMessage(new TextMessage(JsonUtils.toJson(Result.set(CodeEnum.SESSION_ID,session.getId(),null))));
        // 推送在线列表
        pushOnlineUser();
        // 推送公告
        pushNotice(session);
        // 首次连接推送所有消息
        session.sendMessage(new TextMessage(JsonUtils.toJson(Result.set(CodeEnum.SERVER_TO,null,msgList))));
    }

    /**
     *  handleTextMessage: 处理接收到的文本消息。
     * @param session
     * @param message 前端发送的消息
     * @throws Exception
     */
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        log.info("收到 WebSocket 消息：" + message.getPayload().toString());

        Result result = JsonUtils.parse(message.getPayload().toString(),Result.class);
        userMsg userMsg = JsonUtils.parse(result.getData().toString(), userMsg.class);
        String username = userMsg.getUser().getUsername();
        long id = userMsg.getUser().getId();
        if (username == null || "".equals(username)){
            session.sendMessage(new TextMessage(JsonUtils.toJson(Result.error(CodeEnum.NOT_USERNAME))));
        }
       String mtext = userMsg.getMessageInput();
        // 指令 清空消息
        if (mtext.substring(0,1).equals("$")){
            if (mtext.equals("$clear")&&id==1){
                msgList.removeAll(msgList);
                broadcast(JsonUtils.toJson(Result.set(CodeEnum.SERVER_TO,null,msgList)));
                return;
            }
            // 指令 发送公告
            if (mtext.substring(0,3).equals("$gg")&&id==1){
                notice.setTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")));
                notice.setGg(mtext.substring(3));
                broadcast(JsonUtils.toJson(Result.set(CodeEnum.NOTICE,null,notice)));
                return;
            }
        }


        // 普通消息
        Message msg = new Message();
        msg.setId(msgList.size());
        msg.setSender(username);
        msg.setTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")));
        msg.setText(mtext);
        // 广播消息给所有连接的客户端
        msgList.add(msg);
        if (msgList.size()==60)
            msgList.remove(0);
        broadcast(JsonUtils.toJson(Result.set(CodeEnum.MESSAGE,session.getId(),msg)));
    }



    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.info("WebSocket 连接错误：" + session.getId() + ", " + exception.getMessage());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        log.info("WebSocket 连接已关闭：" + session.getId());
        // 移除session id与websocket连接的映射关系
        User user = (User) session.getAttributes().get(session.getId());
        ONLINE_USERS.remove(user);
        String sessionId = (String) session.getAttributes().get("sessionId");
        if (sessionId != null) {
            SESSIONS.remove(sessionId);
            session.close();
        }
        pushOnlineUser();
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    private void broadcast(String message) throws IOException {
        Set<Map.Entry<String, WebSocketSession>> entries = SESSIONS.entrySet();
        for (Map.Entry<String, WebSocketSession> sessions : entries) {
            if(sessions.getValue().isOpen()){
                sessions.getValue().sendMessage(new TextMessage(message));
            }
        }
    }

    // 推送在线列表
    private void pushOnlineUser() throws Exception{

        broadcast(JsonUtils.toJson(Result.set(CodeEnum.ONLINE_USERS,null,ONLINE_USERS)));
    }
    // 推送公告
    private void pushNotice(WebSocketSession session) throws Exception{
        session.sendMessage(new TextMessage(JsonUtils.toJson
                (Result.set(CodeEnum.NOTICE,null,notice))));
    }
    private void delSessionById(long id) throws Exception{
        Set<Map.Entry<String, WebSocketSession>> entries = SESSIONS.entrySet();
        for (Map.Entry<String, WebSocketSession> sessions : entries) {
            User user = (User)sessions.getValue().getAttributes().get(sessions.getValue().getId());
            if (user.getId()==id){
                String sessionId = (String) sessions.getValue().getAttributes().get("sessionId");
                if (sessionId != null) {
                    SESSIONS.remove(sessionId);
                    sessions.getValue().close();
                }
            }
        }
    }
}
