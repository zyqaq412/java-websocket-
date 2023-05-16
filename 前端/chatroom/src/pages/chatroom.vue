<template>

  <div class="chatroom">
    <el-row>
      <el-col :span="6">
        <el-card class="chatroom-users">
          <div class="chatroom-users-header">在线用户</div>
          <div class="chatroom-users-body">
            <div class="chatroom-user" v-for="user in users" :key="user.id" @click="sengById(user)">{{user.username}}</div>
          </div>
        </el-card>
        <el-card class="chatroom-users">
          <div class="chatroom-users-header" >公告</div>
          <div id="gg">
            <div class="preview" v-html="show(notice.gg)"/>
          </div>
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-card class="chatroom-message">
          <div class="chatroom-message-header">{{ roomName }}</div>
          <div class="chatroom-message-body" id="message-box">
            <div class="chatroom-message-item" v-for="message in messages" :key="message.id">
              <div class="chatroom-message-sender">{{ message.sender }}</div>
              <div class="chatroom-message-time">{{ message.time }}</div>
              <div class="chatroom-message-text">
                <div class="preview" v-html="show(message.text)"/>
              </div>

            </div>
          </div>
        </el-card>
        <div class="chatroom-input">

          <mavon-editor id="edit" ref="myEditor" v-model="tomsg.messageInput"
                        defaultOpen="edit"
                        :toolbars="toolbars"
                        @imgAdd="addImg" />
          <el-button type="primary" @click="sendMessage">发送</el-button>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>

window.onbeforeunload = function (e) {
  return e;
};

import MarkdownIt from 'markdown-it'
import mavonEditor from 'mavon-editor'
import 'mavon-editor/dist/css/index.css'
import axios from 'axios';
import { marked } from 'marked'
import hljs from 'highlight.js' // 代码块高亮
import 'highlight.js/styles/github.css' // 代码块高亮样式
import 'github-markdown-css' // 整体 markdown 样式
export default {

  components: {
    'mavon-editor': mavonEditor.mavonEditor,
  },

  data() {
    return {
      // 输入框工具类
      toolbars :{
        preview: true, // 预览
      },
      // 公告
      notice:{
        gg:"",
        time:""
      },
      // 发送消息
      tomsg: {
        user:{
          username:'',
          id:''
        },
        messageInput: ''
      },
      // 收到消息
      sendMsg: {
        code: "",
        id: "",
        msg: "",
        data: {}
      },
      //TODO 暂时用来判断是否需要跳转到消息最下面(以后用用户id判断)
      //存放sessionId 以后用来实现私聊，私聊对象的sessionId(或用户id)
      id: "",
      roomName: "聊天室",
      messages: [
        /*{ id: 1, sender: "张三", time: "10:30", text: "大家好啊！" },*/
      ],
      users: [
        {
          id:'',username:''
        }
      ],
    };
  },
  created() {
    if (!mavonEditor.markdownIt) {
      mavonEditor.markdownIt = new MarkdownIt();
    }
    this.tomsg.user = this.$store.state.user

/*    this.websocket = new WebSocket('ws://localhost:8080/chatroom?username='
        + this.tomsg.user.username+"&id="+this.tomsg.user.id);*/
    this.websocket = new WebSocket('ws://47.113.146.226:8080/chatroom?username='
        + this.tomsg.user.username+"&id="+this.tomsg.user.id);
    this.websocket.addEventListener('open', this.onOpen);
    // 监听 WebSocket 消息事件
    this.websocket.addEventListener('message', this.onMessage);
    // 监听 WebSocket 连接关闭事件
    this.websocket.addEventListener('close', this.onClose);
    // 监听 WebSocket 连接错误事件
    this.websocket.addEventListener('error', this.onError);
  },
  mounted() {

  },
  methods: {
    uploadImg(img) {
      const formData = new FormData()
      formData.append('img', img)
      return axios.post('/upload', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      }).then(response => {
        console.log('上传图片', response.data);
        return response.data.data
      }).catch(error => {
        throw new Error(error.message)
      })
    },
    // 绑定@imgAdd event
    addImg(pos, file) {
      console.log("pos",pos)
      // 第一步.将图片上传到服务器.
      this.uploadImg(file).then(response => {
        // TODO 图片能成功上传，但是这里转成url有问题
        this.$refs.myEditor.$img2Url(pos, response)
      }).catch(error => {
        this.$message.error(error.msg)
      })
    },
    show(text){
      if (text === '' || text ===  undefined) return
      return marked(text, {
        highlight: function (code, lang) {
          const language = hljs.getLanguage(lang) ? lang : 'plaintext';
          return hljs.highlight(code, { language }).value;
        },
      });
    },
    // 私聊 暂不实现
    sengById(user){
      alert("id："+user.id+"\t用户名："+user.username)
    },
    onOpen(event) {
      console.log('WebSocket 连接已打开', event);
    },
    onMessage(event) {
      console.log('收到 WebSocket 消息', event);
      const temp = JSON.parse(event.data)
      console.log('temp', temp);
      if (temp.code === 401) {
        this.$message.error('登录过时请重新登录');
        this.$router.push('/login');
        return
      } else if (temp.code === 1) {
        console.log('返回id',temp.id);
        this.id = temp.id
      } else if (temp.code === 2) {
        console.log(temp.data.text );
        const newMessage = temp.data;
        newMessage.id = this.messages.length + 1;
        this.messages.push(newMessage)
      } else if (temp.code === 0) {
        this.messages = temp.data
      } else if (temp.code === 3) {
        console.log('返回在线列表');
        this.users = temp.data
      } else if (temp.code === 4){
        console.log('公告');
        this.notice = temp.data
      }
      console.log('temp',temp.id);
      console.log('this',this.id);
      if (temp.id === this.id) {
        setTimeout(() => {
          this.moveHuaLun()
        }, 20);
      }
    },
    onClose(event) {
      console.log('WebSocket 连接已关闭', event);
      this.$message.error('WebSocket 连接已关闭');
      this.$router.push('/login');
    },
    onError(event) {
      console.error('WebSocket 连接错误', event);
    },
    sendMessage() {
      if (this.tomsg.messageInput.trim() === "") {
        return;
      }
      this.sendMsg.code = 2;
      this.sendMsg.data = this.tomsg;
      // 发送消息到 WebSocket 服务器
      this.websocket.send(JSON.stringify(this.sendMsg));
      this.tomsg.messageInput = "";
    },
    moveHuaLun() {
      //获取消息框元素
      const messageBox = document.getElementById("message-box");
      if (messageBox) {
        //将滚动条滚动到消息框底部
        messageBox.scrollTop = messageBox.scrollHeight;
      } else {
        console.error("Element with ID 'message-box' not found.");
      }
    }

  },
  beforeUnmount() {
    // 关闭 WebSocket 连接
    this.websocket.close();
  },
};
</script>

<style>
#edit{
  width: 1200px;
  resize: none; /* 禁止拖动 */
}

#gg{
  margin-top: 30px;
  height: 100%;
}
.chatroom {
  margin-left: 5%;
  margin-right: 5%;
  margin-top: 2%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.chatroom-message {
  height: 500px;
  margin-bottom: 10px;
}

.chatroom-message-header {

  font-weight: bold;
  margin-bottom: 5px;
  font-size: 18px;
}

.chatroom-message-body {
  max-height: 460px; /* 最大高度为400像素 */
  overflow-y: auto; /* 显示垂直滚动条，只有在内容溢出时才显示 */
  height: calc(100% - 30px);
  overflow-y: auto;
}

.chatroom-message-item {
  margin-bottom: 10px;
}

.chatroom-message-sender {
  font-weight: bold;
  margin-right: 5px;
}

.chatroom-message-time {

  font-weight: normal;
}

.chatroom-message-text {
  word-break: break-all;
  border: 1px solid black;
  background-color: aliceblue;
  zoom: 0.9;
  padding: 5px;
}

.chatroom-input {
  display: flex;
  margin-top: 20px;
  justify-content: space-between;
}

.chatroom-users {
  max-height: 400px; /* 最大高度为400像素 */
  max-width: 260px;
  overflow-y: auto; /* 显示垂直滚动条，只有在内容溢出时才显示 */
  height: 100%;
}

.chatroom-users-header {
  /* 指定文本的粗细程度 */
  width: 100px;
  font-weight: bold;
  margin-bottom: 5px;
  font-size: 18px;
}

.chatroom-users-body {
  height: calc(100% - 30px);
  overflow-y: auto;
}

.chatroom-user {
  margin-bottom: 5px;
  cursor: pointer;
}
</style>

