<template>
    <ContentCard>
        <table class="table table-hover">
            <thead>
                <th> A</th>
                <th> B</th>
                <th> Match Result </th>
                <th> Time </th>
                <th> ... </th>
            </thead>
            <tbody>
                <tr v-for="replay in replays" :key="replay.replay.id">
                    <td>
                        <img :src="replay.a_photo" alt="" class="replay-user-photo">
                        &nbsp;
                        <span class="replay-user-username">{{ replay.a_username }}</span>
                    </td>
                    <td>
                        <img :src="replay.b_photo" alt="" class="replay-user-photo">
                        &nbsp;
                        <span class="replay-user-username">{{ replay.b_username }}</span>
                    </td>
                    <td>
                        {{replay.result}}
                    </td>
                    <td>
                        {{replay.replay.createTime}}
                    </td>
                    <td>
                        <button @click="open_replay_content(replay.replay.id)" class="btn btn-primary">Watch the replay</button>
                    </td>
                </tr>    
            </tbody>
        </table>

        <nav aria-label="...">
            <ul class="pagination" style="float: right">
                <li :class="'prev_disabled'" @click="click_page(-2)">
                    <a class="page-link" href="#">Previous</a>
                </li>
                <li :class="'page_item ' + page.is_active" v-for="page in pages" :key="page.number" @click="click_page(page.number)">
                    <a class="page-link" href="#"> {{ page.number }}</a>
                </li>
                <li :class="'next_disabled'" @click="click_page(-1)">
                <a class="page-link" href="#">Next</a>
                </li>
            </ul>
        </nav>
    </ContentCard>
</template>

<script>
import ContentCard from "@/components/ContentCard.vue";
import { useStore } from "vuex";
import $ from "jquery";
import { ref } from "vue";
import router from "@/router/index"

export default {
    components: {
        ContentCard
    },
    setup() {
        const store = useStore();
        let replays = ref([]);
        let current_page = 1;
        let total_replays = 0;
        let max_pages = 0;
        let prev_disabled = ref();
        let next_disabled = ref();
        let pages = ref([]);

        const click_page = page => {
            if (page === -2) page = current_page - 1;
            if (page === -1) page = current_page + 1;
            
            if (page >= 1 && page <= max_pages) {
                pull_page(page);
            }
        }

        const update_pages = () => {
            let new_pages = [];
            for (let i = current_page - 2; i <= current_page + 2; i++) {
                if (i >= 1 && i <= max_pages) {
                    new_pages.push({
                        number: i,
                        is_active: i === current_page ? "active" : "inactive",
                    });
                }
            }
            pages.value = new_pages;
        }


        const pull_page = page => {
            current_page = page;
            $.ajax({
                url: "http://localhost:8888/replay/getlist/",
                data: {
                    page,
                },
                type: "GET",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp) {
                    replays.value = resp.replays;
                    total_replays = resp.replays_count;
                    max_pages = parseInt(Math.ceil(total_replays / 10));
                    prev_disabled = 1 === current_page? "page-item disabled": "page-item ";
                    next_disabled = max_pages === current_page? "page-item disabled": "page-item ";
                    console.log(prev_disabled)
                    console.log(next_disabled)
                    update_pages();
                },
                error(resp) {
                    console.log(resp);
                }
            })
        }
        pull_page(current_page);

        const open_replay_content = replayId => {
            for (const replay of replays.value) {
                if (replay.replay.id === replayId) {
                    console.log(replay)
                    store.commit("updateIsReplay", true);
                    const map2d = JSON.parse(replay.replay.map).map(innerArray => innerArray.map(Number));
                    store.commit("updateGame", {
                        map: map2d,
                        a_id: replay.replay.aid,
                        a_sx: replay.replay.asx,
                        a_sy: replay.replay.asy,
                        b_id: replay.replay.bid,
                        b_sx: replay.replay.bsx,
                        b_sy: replay.replay.bsy,
                    });
                    store.commit("updateSteps", {
                        a_steps: replay.replay.asteps,
                        b_steps: replay.replay.bsteps,
                    })
                    console.log(replay.replay.loser)
                    store.commit("updateReplayLoser", replay.replay.loser)
                    router.push({
                        name: "replay_content",
                        params: {
                            replayId
                        }
                        
                    })
                    break;
                }
            }
        }

        return {
            replays,
            total_replays,
            max_pages,
            open_replay_content,
            pages,
            prev_disabled,
            next_disabled,
            click_page,
        }   
    }

}

</script>

<style scoped>
img.replay-user-photo {
    width: 4vh;
    border-radius: 50%;

}
</style>