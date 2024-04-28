<template>
    <div class="result-board">
        <div class="match-result-text" v-if="$store.state.pk.loser === 'All'">
            TIE
        </div>
        <div class="match-result-text" v-else-if="$store.state.pk.loser === 'A' && $store.state.pk.player.a.id === parseInt($store.state.user.id)">
            YOU DIE
        </div>
        <div class="match-result-text" v-else-if="$store.state.pk.loser === 'B' && $store.state.pk.player.b.id === parseInt($store.state.user.id)">
            YOU DIE
        </div>
        <div class="match-result-text" v-else>
            WINNER WINNER CHIKEN DINNER
        </div>
        <div class="result-board-btn">
            <button @click="restart" type="button" class="btn btn-light btn-xl">
            Play Again!
            </button>
        </div>
    </div>
</template>

<script>
import { useStore } from 'vuex';

export default {
    setup() {
        const store = useStore()
        const restart = () => {
            store.commit("updateStatus", "Matching");
            store.commit("cleanup");
        }

        return {
            restart,
        }
    }
}
</script>

<style scoped>
div.result-board {
    height: 50vh;
    width: 60vw;
    background-color: rgb(255, 255, 0, 0.3);
    position: absolute;
    top: 25vh;
    left: 20vw;
}
div.match-result-text {
    text-align: center;
    color: gold;
    font-size: 50px;
    font-weight: 600;
    font-style: italic;
    padding-top: 5vh;

}

div.result-board-btn {
    text-align: center;
    padding-top: 1vh;
}

</style>