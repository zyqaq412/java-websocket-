import Vue from "vue"
import Vuex from "vuex"

Vue.use(Vuex);

export default new Vuex.Store({
    state:{
        user:{
            username:'',
            id:''
        }
    },
    mutations:{
        setUsername(state,user){
            state.user=user
        }
    }
})
