
export default {
    state: {
        status: "Matching", // one of Matching and Playing
        socket: null,
        opponent_username: "",
        opponent_photo: "",
        game_map : null,
        player: {
            a: {
                id: 0,
                sx: 0,
                sy: 0,
            },
            b: {
                id: 0,
                sx: 0,
                sy: 0,
            }
        },
        gameObject: null,
        loser: "", // one of {A, B, All}
    },
    getters: {

    },

    mutations: {
        updateSocket(state, socket) {
            state.socket = socket;
        },

        updateOpponent(state, opponent) {
            state.opponent_username = opponent.username;
            state.opponent_photo = opponent.photo;
        },

        updateStatus(state, status) {
            state.status = status;
        },
        updateGame(state, game) {
            state.game_map = game.map;
            state.player.a.id = game.a_id;
            state.player.a.sx = game.a_sx;
            state.player.a.sy = game.a_sy;
            state.player.b.id = game.b_id;
            state.player.b.sx = game.b_sx;
            state.player.b.sy = game.b_sy;
        },
        updateGameObject(state, GameObject) {
            state.gameObject = GameObject;
        },
        updateLoser(state, loser) {
            state.loser = loser;
        },
        cleanup(state) {
           state.status = "Matching";
           state.opponent_username = '???';
           state.opponent_photo =  "https://cdn.acwing.com/media/article/image/2022/08/09/1_1db2488f17-anonymous.png";
           state.game_map = null;
           state.player.a.id = 0;
           state.player.a.sx = 0;
           state.player.a.sy = 0;
           state.player.b.id = 0;
           state.player.b.sx = 0;
           state.player.b.sy = 0;
           state.gameObject = null;
           state.loser = ""
        }

    },
    actions: {
    },
    modules: {
    }
}