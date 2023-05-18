<template>
  <div>
    <div id="bt">
      <h1>登录</h1>
    </div >
    <div class="login">
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户名" prop="userAccount">
          <el-input v-model="form.userAccount"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="userPassword">
          <el-input type="password" v-model="form.userPassword"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="login">登录</el-button>
          <el-button type="primary">
            <router-link to="/register" style="text-decoration: none;color: white">注册
            </router-link></el-button>
        </el-form-item>
      </el-form>

    </div>
  </div>
</template>

<script>
import axios from "axios";
export default {
  data() {
    return {
      form: {
        userAccount: '',
        userPassword: ''
      },
      rules: {
        userAccount: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        userPassword: [{ required: true, message: '请输入密码', trigger: 'blur' }]
      }
    }
  },
  methods: {
    login() {
      this.$message({
        message: '请等待',
        type: 'success'
      });
      axios.post('/user/login', {
        userAccount: this.form.userAccount,
        userPassword: this.form.userPassword,
      }).then(res => {
        // 处理注册成功的逻辑
        if (res.data.code == 200){
          console.log("登录成功")
          this.$message({
            message: '登录成功',
            type: 'success'
          });
          console.log(res.data.data)
          this.$store.commit('setUsername',res.data.data)
          console.log(this.$store.state.username)
          this.$router.push('/chatroom');
        }
      })
    }
  }
}
</script>

<style>
.login{
  display: flex;
  justify-content: center; /* 水平居中 */
  align-items: center; /* 垂直居中 */
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}
#bt{
  justify-content: center; /* 水平居中 */
  position: absolute;
  left: 50%;
  top: 20%;

}

</style>
