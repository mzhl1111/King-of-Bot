const GAME_OBJECT = [];



export class GameObject {
    constructor() {
        GAME_OBJECT.push(this);
        this.time_delta = 0;
        this.has_called_start = false
    }

    start () { // only call on start for once

    }

    update() { // calls every frame, except the start

    }

    on_destroy() { // calls before destory

    }

    destory() {
        for (let i in GAME_OBJECT){
            const obj = GAME_OBJECT[i];
            if (obj == this) {
                GAME_OBJECT.splice(i);
                break;
            }
        }
    }
}

let last_timestamp;
const step = timestamp => {
    for (let obj of GAME_OBJECT) {
        if (!obj.has_called_start) {
            obj.has_called_start = true;
            obj.start()
        } else {
            obj.time_delta = timestamp -last_timestamp;
            obj.update();
        }
    }

    last_timestamp = timestamp
    requestAnimationFrame(step)
}

requestAnimationFrame(step)