
export default {
    state: {
       is_replay: false,
       a_steps: "",
       b_steps: "",
       replay_loser: "",
    },
    getters: {

    },

    mutations: {
        updateIsReplay(state, is_replay) {
            state.is_replay = is_replay;
        },
        
        updateSteps(state, data) {
            state.a_steps = data.a_steps;
            state.b_steps = data.b_steps;
        },

        updateReplayLoser(state, replay_loser) {
            state.replay_loser = replay_loser;
        }
    },
    actions: {
    },
    modules: {
    }
}