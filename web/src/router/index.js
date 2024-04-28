import { createRouter, createWebHistory } from 'vue-router'

import pkIndexView from '@/views/pk/pkIndexView'
import replayIndexView from "@/views/replay/replayIndexView"
import replayContentView from "@/views/replay/replayContentView"
import rankIndexView from "@/views/rank/rankIndexView"
import userBotIndexView from "@/views/user/bots/userBotIndexView"
import notFound from "@/views/error/notFoundView"
import UserAccountLoginView from "@/views/account/UserAccountLoginView"
import UserAccountRegisterView from  "@/views/account/UserAccountRegisterView"
import store from "@/store/index"

const routes = [
  {
    path: "/",
    name: "home",
    redirect: "/pk/",
    meta: {
      requestAuth: true,
    }
  },
  {
    path: "/pk/",
    name: "pk_index",
    component: pkIndexView,
    meta: {
      requestAuth: true,
    }
  },
  {
    path: "/replay/",
    name: "replay_index",
    component: replayIndexView,
    meta: {
      requestAuth: true,
    }
  },
  {
    path: "/replay/:replayId",
    name: "replay_content",
    component: replayContentView,
    meta: {
      requestAuth: true,
    }
  },
  {
    path: "/ranks/",
    name: "rank_index",
    component: rankIndexView,
    meta: {
      requestAuth: true,
    }
  },
  {
    path: "/user/account/login/",
    name: "user_account_login",
    component: UserAccountLoginView,
    meta: {
      requestAuth: false,
    }
  },
  {
    path: "/user/account/register/",
    name: "user_account_register",
    component: UserAccountRegisterView,
    meta: {
      requestAuth: false,
    }
  },
  {
    path: "/user/bots/",
    name: "user_bot_index",
    component: userBotIndexView,
    meta: {
      requestAuth: true,
    }
  },
  {
    path: "/404/",
    name: "404",
    component: notFound,
    meta: {
      requestAuth: false,
    }
  },
  {
    path: "/:catchAll(.*)",
    
    redirect: "404",
  }, 
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  if(to.meta.requestAuth && !store.state.user.logged_in) {
    next({name: "user_account_login"});
  } else {
    next();
  }
})

export default router
