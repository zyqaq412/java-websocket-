import Vue from 'vue'
import App from './App.vue'
import router from './router/router' // 导入 router 实例
// 引入vuex-store
import store from './store/store';
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import axios from 'axios';
// 配置axios请求的根路径
axios.defaults.baseURL = 'http://localhost:8888';
Vue.use(ElementUI)
Vue.config.productionTip = false
new Vue({
  store,
  router, // 注册 router 实例
  render: h => h(App)
}).$mount('#app')
