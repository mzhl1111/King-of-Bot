<template>
    <ContentCard v-if="!$store.state.user.pulling_info">
      <div class="row justify-content-md-center">
        <div class="col-3">
          <form @submit.prevent="login">
            <div class="mb-3">
              <label for="username" class="form-label">Username</label>
              <input v-model="username" type="text" class="form-control" id="username" aria-describedby="usernameHelp" placeholder="please input username">
            </div>
            <div class="mb-3">
              <label for="password" class="form-label">Password</label>
              <input v-model="password" type="password" class="form-control" id="password" placeholder="please input password">
            </div>
            <div class="error_message">{{ error_message }}</div>
            <button type="submit" class="btn btn-primary">Login</button>
          </form>
        </div>
      </div>
    </ContentCard>
  </template>
   
  
<script>
import ContentCard from "@/components/ContentCard"
import { useStore } from "vuex"
import { ref } from "vue"
import router from "@/router/index"

export default {
    components: {
        ContentCard
    },
    setup() {
      const store = useStore();
      let username = ref('');
      let password = ref('');
      let error_message = ref('');
      let show_content = ref(false);

      const jwt_token = localStorage.getItem("jwt_token")
      if (jwt_token) {
        store.commit('updateToken', jwt_token);
        store.dispatch('getInfo', {
          success() {
            router.push({name: "home"})
            store.commit("updatePullingInfo", false)
          },
          error() {
            store.commit("updatePullingInfo", false)
          }
        })
      } else {
        store.commit("updatePullingInfo", false);
      }

      const login = () => {
        error_message.value = "";
        store.dispatch("login", {
          username: username.value,
          password: password.value,
          success() {
            store.dispatch("getInfo", {
              success() {
                router.push({ name: "home" });
              }
            })
          },
          error() {
            error_message.value = "Invalid username or password";
          }
        })

      }

      return {
        username,
        password,
        error_message,
        show_content,
        login,
      }

    }
}
</script>

  
<style scoped>

button {
  width: 100%;
}

div.error_message {
  color: red;
  padding-bottom: 5px;
}
</style>