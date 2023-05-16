<template>
  <div>
    <div id="zc">
      <h1>注册</h1>
    </div>

    <div class="register">

      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户名" prop="userAccount">
          <el-input v-model="form.userAccount"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email"></el-input>
        </el-form-item>
        <el-form-item label="验证码" prop="code">
          <el-row>
            <el-col :span="16">
              <el-input v-model="form.code"></el-input>
            </el-col>
            <el-col :span="8">
              <el-button @click="getCode">获取验证码</el-button>
            </el-col>
          </el-row>
        </el-form-item>
        <el-form-item label="密码" prop="userPassword">
          <el-input type="userPassword" v-model="form.userPassword"></el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="checkPassword">
          <el-input type="userPassword" v-model="form.checkPassword"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="register">注册</el-button>
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
        email: '',
        code: '',
        userPassword: '',
        checkPassword: ''
      },
      rules: {
        userAccount: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
        ],
        code: [{ required: true, message: '请输入验证码', trigger: 'blur' }],
        userPassword: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
        ],
        checkPassword: [
          { required: true, message: '请再次输入密码', trigger: 'blur' },
          {
            validator: (rule, value, callback) => {
              if (value === '') {
                callback(new Error('请再次输入密码'))
              } else if (value !== this.form.userPassword) {
                callback(new Error('两次输入的密码不一致'))
              } else {
                callback()
              }
            },
            trigger: 'blur'
          }
        ]
      }
    }
  },
  methods: {
    register() {
      axios.post('/user/register', {
        userAccount: this.form.userAccount,
        email: this.form.email,
        userPassword: this.form.userPassword,
        checkPassword: this.form.checkPassword,
        code: this.form.code
      })
          .then(res => {
            // 处理注册成功的逻辑
            if (res.data.code===200){
              console.log("注册成功")
              this.$router.push('/login');
            }
          })
    },
    getCode() {
      axios.post('/mail',{
        to: this.form.email
      }).then(res =>{
        if (res.data.code == 200){
          console.log("验证码发送成功")
          this.$message({
            message: '验证码发送成功',
            type: 'success'
          });
        }else {
          this.$message.error('验证码发送失败');
        }
      })
    }
  }
}
</script>
<style>
.register{
  display: flex;
  justify-content: center; /* 水平居中 */
  align-items: center; /* 垂直居中 */
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}
#zc{
  justify-content: center; /* 水平居中 */
  position: absolute;
  left: 50%;
  top: 10%;
}

</style>
