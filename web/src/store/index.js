import { createStore } from 'vuex'
import ModuleUser from "./user"
import ModulePk from "./pk"
import ModuleReplay from "./replay"


export default createStore({
  state: {
  },
  getters: {
  },
  mutations: {
  },
  actions: {
  },
  modules: {
    user: ModuleUser,
    pk: ModulePk,
    replay: ModuleReplay,
  }
})
