import { Cell } from "./Cell";
import { GameObject } from "./GameObject";


export class Snake extends GameObject{
    constructor(info, gamemap){
        super();

        this.id = info.id;
        this.color = info.color;
        this.gamemap = gamemap;

        this.cells = [new Cell(info.r, info.c)] // saves the body of the snake, cell[0] represnts the head
        this.next_cell = null; // the target grid for nex step

        this.speed = 5 // snake moves 5 cell per second
        this.direction = -1 // -1 represents no command, 0, 1, 2, 3 for up, right, down, left
        this.status = "idle" // idle reprenst no movement, move for moving and die for die


        this.dr = [-1, 0, 1, 0];
        this.dc = [0, 1, 0, -1]; // combined as moving up, right, down and left

        this.rounds = 0; // # of rounds
        this.eps = 1e-2;

        this.eye_direction = 0;
        if (this.id === 1) this.eye_direction = 2; // blue snake initialzed with eyes up and red one initially with eyes down

        this.eye_position = {
            dx : [
                [-1, 1],
                [1, 1],
                [-1, 1],
                [-1, -1],
            ],
            dy:[
                [-1, -1],
                [-1, 1],
                [1, 1],
                [1, -1],
            ]
        }
    }

    set_direction(d) {
        this.direction = d;
    }

    check_tail_increasing(){ // the length of snake always increas for first 10 round and then increase every 3 round afater 10 (13, 16, 19, .....)
        if (this.rounds <= 10) return true;
        if (this.rounds % 3 === 1) return true;

        return false;
    }

    start() {

    }

    next_step() { // change the status of stake to next round
        const d = this.direction;
        this.next_cell = new Cell(this.cells[0].r + this.dr[d], this.cells[0].c + this.dc[d]);
        this.eye_direction = d;
        this.direction = -1;
        this.status = "move";
        this.rounds ++;

        const k = this.cells.length;
        for (let i = k; i > 0; i--) {
            this.cells[i] = JSON.parse(JSON.stringify(this.cells[i-1]));
        }
        if (!this.gamemap.judge(this.next_cell)) { // next step hist something and kills the snake
            this.status = "die"
        }
    }

    update_move() {

        const dx = this.next_cell.x - this.cells[0].x;
        const dy = this.next_cell.y - this.cells[0].y;
        const distance = Math.sqrt(dx * dx + dy * dy);

        if (distance < this.eps) {
            this.cells[0] = this.next_cell; // update the snake head
            this.next_cell = null; 
            this.status = "idle" // mark as movement ends

            if(!this.check_tail_increasing()){
                this.cells.pop();
            }
        } else {
            const move_distance_per_frame = this.speed * this.time_delta / 1000; // move distance between two frame
            this.cells[0].x += move_distance_per_frame * dx / distance;
            this.cells[0].y += move_distance_per_frame * dy / distance;

            if (!this.check_tail_increasing()) {
                const k  = this.cells.length;
                const tail = this.cells[k-1], tail_target = this.cells[k-2];
                const tail_dx = tail_target.x - tail.x;
                const tail_dy = tail_target.y - tail.y;
                tail.x += move_distance_per_frame * tail_dx / distance;
                tail.y += move_distance_per_frame * tail_dy / distance;
            }
        }


    }

    update() {
        if (this.status === "move") {
            this.update_move();
        }
        this.render();
    }

    equals (x, y){
        if (Math.abs(x - y) < this.eps) return true;
        return false;
    }

    render() {
        const L = this.gamemap.L;
        const snake_width = L * 0.8
        const eye_radius = L * 0.1
        const eye_ball_radius = L * 0.04
        const dead_eye_radius = L * 0.07
        const ctx = this.gamemap.ctx;

        ctx.fillStyle = this.color;
        if (this.status === "die") ctx.fillStyle = "white"
        for (const cell of this.cells) {
            ctx.beginPath();
            ctx.arc(cell.x * L, cell.y * L, snake_width / 2, 0, Math.PI * 2);
            ctx.fill();
        }

        for (let i = 1; i < this.cells.length; i++) {
            const a = this.cells[i - 1], b= this.cells[i];
            if(this.equals(a.x, b.x) && this.equals(a.y, b.y))
                continue;
            if (this.equals(a.x, b.x)){
                ctx.fillRect((a.x - 0.4) * L, Math.min(a.y, b.y) * L, snake_width, Math.abs(a.y - b.y) * L);
            } else {
                ctx.fillRect(Math.min(a.x, b.x) * L, (a.y - 0.4) * L, Math.abs(a.x - b.x) * L, snake_width);
            }
        }

        if (this.status !== "die") {
            ctx.fillStyle = "#FFFECA";
            for (let i = 0; i < 2; i++) {
                const eye_x = this.cells[0].x + this.eye_position.dx[this.eye_direction][i] * 0.15;
                const eye_y = this.cells[0].y + this.eye_position.dy[this.eye_direction][i] * 0.15;
                ctx.beginPath();
                ctx.ellipse(eye_x * L, eye_y * L, eye_radius, eye_radius * 0.7, Math.PI / 2 , 0, Math.PI * 2);
                ctx.fill();
                ctx.closePath();
            }
            ctx.fillStyle = "black"
            for (let i = 0; i < 2; i++) {
                const eye_x = this.cells[0].x + this.eye_position.dx[this.eye_direction][i] * 0.15;
                const eye_y = this.cells[0].y + this.eye_position.dy[this.eye_direction][i] * 0.18;
                ctx.beginPath();
                ctx.arc(eye_x * L, eye_y * L, eye_ball_radius, 0, Math.PI * 2);
                ctx.fill();
                ctx.closePath();
            }
        } else {
            ctx.strokeStyle = "black";
            for (let i = 0; i < 2; i++) {
                const eye_x = this.cells[0].x + this.eye_position.dx[this.eye_direction][i] * 0.15;
                const eye_y = this.cells[0].y + this.eye_position.dy[this.eye_direction][i] * 0.18;
                ctx.beginPath();
                ctx.lineWidth = eye_ball_radius;
                ctx.moveTo(eye_x * L + dead_eye_radius, eye_y * L - dead_eye_radius);
                ctx.lineTo(eye_x * L - dead_eye_radius, eye_y * L + dead_eye_radius);

                ctx.moveTo(eye_x * L - dead_eye_radius, eye_y * L - dead_eye_radius);
                ctx.lineTo(eye_x * L + dead_eye_radius, eye_y * L + dead_eye_radius);
                ctx.stroke();
            }
        }
    }
}