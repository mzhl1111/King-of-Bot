import $ from "jquery"

export default {
    state: {
        id: "",
        username: "",
        photo: "",
        token: "",
        logged_in : false,
        pulling_info: true,
    },
    getters: {

    },

    mutations: {
        updateUser(state, user) {
            state.id = user.id;
            state.username = user.username;
            state.photo = user.photo;
            state.logged_in = user.logged_in;
            console.log(state)
        },
        updateToken(state, token) {
            state.token = token;
        },
        logout(state) {
            state.id = "";
            state.username = "";
            state.photo = "";
            state.token = "";
            state.logged_in = "";
        },
        updatePullingInfo(state, pulling_info) {
            state.pulling_info = pulling_info;
        },
    },
    actions: {
        login(context, data) {
            $.ajax({
                url: "http://localhost:8888/user/account/token/",
                type: "post",
                data: {
                    username: data.username,
                    password: data.password,
                },
                success(resp) {
                    if(resp.error_message === "success"){
                        context.commit("updateToken", resp.token);
                        localStorage.setItem("jwt_token", resp.token)
                        data.success(resp);
                    } else {
                        data.error(resp);
                    }
                },
                error(resp) {
                    data.error(resp);
                }
            })
        },
        getInfo(context, data) {
            $.ajax({
                url: "http://localhost:8888/user/account/info/",
                type: "get",
                headers: {
                    Authorization: "Bearer " + context.state.token,
                },
                success(resp){
                    if (resp.error_message === "success") {
                        context.commit("updateUser", {
                            ...resp,
                            logged_in: true,
                        })
                        data.success(resp);
                    } else {
                        data.error(resp);
                    }
                },
                error(resp){
                    data.error(resp);
                }
            })
        },
        logout(context) {
            localStorage.removeItem("jwt_token");
            context.commit("logout");

        },
    },
    modules: {
    }
}