<template>
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
        <div class="container-fluid">
            <router-link class="navbar-brand" :to="{name: 'home'}">King of Bots</router-link>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarText" aria-controls="navbarText" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarText">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item">
                  <router-link :class="route_name == 'pk_index' ? 'nav-link active' : 'nav-link'" :to="{name: 'pk_index'}">VS</router-link>
                </li>
                <li class="nav-item">
                  <router-link :class="route_name == 'replay_index' ? 'nav-link active' : 'nav-link'" :to="{name: 'replay_index'}">Replays</router-link>
                </li>
                <li class="nav-item">
                  <router-link :class="route_name == 'rank_index' ? 'nav-link active' : 'nav-link'" :to="{name: 'rank_index'}">Rank</router-link>
                </li>
            </ul>
            <ul class="navbar-nav right-corner" v-if="$store.state.user.logged_in">
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                        {{ $store.state.user.username }}
                    </a>
                    <ul class="dropdown-menu">
                        <li>
                            <router-link class="dropdown-item" :to="{name: 'user_bot_index'}">My Bot</router-link>
                        </li>
                        <li>
                            <a class="dropdown-item" href="#" @click="logout">Log Out</a>
                        </li>
                    </ul>
                </li>
            </ul>
            <ul class="navbar-nav right-corner" v-else-if="!$store.state.user.pulling_info">
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                        User
                    </a>
                    <ul class="dropdown-menu">
                        <li>
                            <router-link class="dropdown-item" :to="{name: 'user_account_login'}">Login</router-link>
                        </li>
                        <li>
                            <router-link class="dropdown-item" :to="{name: 'user_account_register'}" href="#">Register</router-link>
                        </li>
                    </ul>
                </li>
            </ul>
            </div>
        </div>
    </nav>
</template>

<script>
import { useRoute } from 'vue-router';
import { computed } from 'vue';
import { useStore } from 'vuex';

export default {
    setup() {
        const store = useStore();
        const route = useRoute();
        let route_name  = computed(()=> route.name)

        const logout = () => {
            store.dispatch("logout")
        }

        return {
            route_name,
            logout,
        }
    }
}
</script>

<style scoped>
.right-corner {
    margin-right: 80px;
}

.dropdown {
    width: 80px;
}
</style>