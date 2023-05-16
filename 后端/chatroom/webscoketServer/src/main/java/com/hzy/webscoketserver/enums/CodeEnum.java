package com.hzy.webscoketserver.enums;

/**
 * @title: CodeEnum
 * @Author zxwyhzy
 * @Date: 2023/4/28 14:54
 * @Version 1.0
 */
public enum CodeEnum {
    // 成功
    SERVER_TO(0,"首次连接，推送消息"),
    SESSION_ID(1,"连接id"),
    MESSAGE(2,"消息"),
    ONLINE_USERS(3,"在线用户"),
    NOTICE(4,"公告"),
    NOT_USERNAME(401,"没有用户名"),
    SUCCESS(200,"操作成功");



    int code;
    String msg;

    CodeEnum(int code, String errorMessage) {
        this.code = code;
        this.msg = errorMessage;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
