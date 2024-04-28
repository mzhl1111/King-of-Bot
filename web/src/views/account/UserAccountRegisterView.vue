<template>
  <ContentCard>
    <div class="row justify-content-md-center">
      <div class="col-3">
        <form @submit.prevent="register">
          <div class="mb-3">
            <label for="username" class="form-label">Username</label>
            <input v-model="username" type="text" class="form-control" id="username" aria-describedby="usernameHelp" placeholder="please input username">
          </div>
          <div class="mb-3">
            <label for="password" class="form-label">Password</label>
            <input v-model="password" type="password" class="form-control" id="password" placeholder="please input password">
          </div>
          <div class="mb-3">
            <label for="confirmPwd" class="form-label">Confirm password</label>
            <input v-model="confirmPwd" type="password" class="form-control" id="confirmPwd" placeholder="please confirm your password">
          </div>
          <div class="error_message">{{ error_message }}</div>
          <button type="submit" class="btn btn-primary">register</button>
        </form>
      </div>
    </div>
  </ContentCard>
</template>
 

<script>
import ContentCard from "@/components/ContentCard"
import { ref } from "vue"
import router from "@/router/index"
import $ from "jquery"


export default {
  components: {
      ContentCard
  },
  setup() {
    let username = ref('');
    let password = ref('');
    let confirmPwd = ref('');
    let error_message = ref('');

    const register = () => {
      error_message.value = "";

            $.ajax({
                url: "http://localhost:8888/user/account/register/",
                type: "post",
                data: {
                    username: username.value,
                    password: password.value,
                    confirmPwd: confirmPwd.value,
                },
                success(resp) {
                    if(resp.error_message === "success"){
                      router.push({name: "home"})
                    } else {
                        error_message.value = resp.error_message;
                    }
                },
                error(resp) {
                    error_message.value = resp.error_message;
                }
            })
    }

    return {
      username,
      password,
      confirmPwd,
      error_message,
      register,
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