import { GameObject } from "./GameObject";
import { Wall } from "./wall";
import { Snake } from "./Snake";

export class GameMap extends GameObject {
    constructor(ctx, parent, store) {
        super();


        this.ctx = ctx;
        this.parent = parent;
        this.store = store;
        this.L = 0;

        this.rows = 13;
        this.cols = 14;

        this.walls = [];
        this.inner_wall_count = 20;

        this.snakes = [
            new Snake({id: 0, color: "#4876EC", r: this.rows - 2, c: 1}, this),
            new Snake({id: 1, color: "#F94848", r: 1, c: this.cols - 2}, this),
        ];
    }



    create_walls() {
        const g = this.store.state.pk.game_map;

        for(let r = 0; r < this.rows; r++) {
            for(let c = 0; c < this.cols; c++) {
                if(g[r][c]) {
                    this.walls.push(new Wall(r, c, this))
                }
            }
        }

        return true;
    }


    add_listening_events() {
        if (this.store.state.replay.is_replay) {
            let k = 0;

            console.log(this.store.state.replay)
            const a_steps = JSON.parse(this.store.state.replay.a_steps);
            const b_steps = JSON.parse(this.store.state.replay.b_steps);
            const [snake0, snake1] = this.snakes;
            const loser = this.store.state.replay.replay_loser;
            const interval_id = setInterval(() => {
                if (k >= a_steps.length - 1) { // last step is a die operation, do not need to replay;
                    console.log(loser)
                    if (loser === "All" || loser === "A") {
                        snake0.status = "die";
                    }
                    if (loser === "All" || loser === "B") {
                        snake1.status = "die";
                    }
                    clearInterval(interval_id);
                } else {
                    snake0.set_direction(a_steps[k]);
                    snake1.set_direction(b_steps[k]);
                    k = k + 1;
                }
            }, 300)
        } else  {
            this.ctx.canvas.focus();
            this.ctx.canvas.addEventListener("keydown", e => {
                let d = 0;
                if (e.key === "w") d = 0;
                else if (e.key === "d") d = 1;
                else if (e.key === "s") d = 2;
                else if (e.key === "a") d = 3;
    
                if (d >= 0) {
                    this.store.state.pk.socket.send(JSON.stringify({
                        event: "move",
                        direction: d,
                    }))
                }
            })
        }
    }


    start() {
        for(let i =0; i < 1000; i++){
            if (this.create_walls()) break;
        }

        this.add_listening_events();

    }

    update_size() {
        this.L = parseInt(Math.min(this.parent.clientWidth / this.cols, this.parent.clientHeight / this.rows));
        this.ctx.canvas.width = this.L * this.cols;
        this.ctx.canvas.height = this.L * this.rows;
    }


    check_ready() { //judge if two snake is ready for the next round
        for (const snake of this.snakes) {
            if (snake.status !== "idle") return false;
            if (snake.direction === -1) return false;
        }
        return true;
    }

    next_step() { // let the two snake goes into next round
        for (const snake of this.snakes) {
            snake.next_step();  
        }
    }

    judge(cell) { // judge if cell is a valid moving target
        for (const wall of this.walls) {
            if (wall.r === cell.r && wall.c === cell.c){
                return false;
            }
        }

        for (const snake of this.snakes) {
            let k = snake.cells.length;
            if (!snake.check_tail_increasing()) {
                k --;
            }
            for (let i = 0; i < k; i++) {
                if (snake.cells[i].r === cell.r && snake.cells[i].c === cell.c) {
                    return false;
                }
            }
        }

        return true;
    }

    update() {
        this.update_size();
        this.render();
        if (this.check_ready()) {
            this.next_step();
        }

    }

    render() {
        const color_even = "#82EAFD", color_odd = "#02BADA";
        for (let r = 0; r < this.rows; r++){
            for (let c = 0; c < this.cols; c++) {
                if ((r + c) % 2 == 0) {
                    this.ctx.fillStyle = color_even;
                } else {
                    this.ctx.fillStyle = color_odd;
                }
                this.ctx.fillRect(c * this.L, r * this.L, this.L, this.L)
            }
        }
    }
}