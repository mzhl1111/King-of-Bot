<template>
    <div class="container">
      <div class="row">
        <div class="col-3">
          <div class="card" style="margin-top: 20px;">
            <div class="card-body">
              <img :src="$store.state.user.photo" alt="" style="width: 100%;">
            </div>
          </div>
        </div>
        <div class="col-9">
          <div class="card" style="margin-top: 20px;">
            <div class="cardheader">
              <span style="font-size: 130%;"> My Bots</span>
              <button type="button" class="btn btn-primary float-end" data-bs-toggle="modal" data-bs-target="#add-bot-btn" style="margin-top: 5px; margin-right: 5px;">+ New Bot</button>
              <!-- Modal -->
              <div class="modal modal-xl modal-dialog-scrollable" id="add-bot-btn" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
                <div class="modal-dialog">
                  <div class="modal-content">
                    <div class="modal-header">
                      <h1 class="modal-title fs-5" id="exampleModalLabel">Create new bot</h1>
                      <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                      <!-- form -->
                      <div class="mb-3">
                        <label for="add-bot-name" class="form-label">Bot name</label>
                        <input v-model="bot_to_add.botName" type="text" class="form-control" id="exampleFormControlInput1" placeholder="Please input bot name">
                      </div>
                      <div class="mb-3">
                        <label for="add-bot-descrption" class="form-label">Description</label>
                        <textarea v-model="bot_to_add.description" class="form-control" id="exampleFormControlTextarea1" rows="3" placeholder="Please bot's description"></textarea>
                      </div>
                      <div class="mb-3">
                        <label for="add-bot-descrption" class="form-label">Code</label>
                        <VAceEditor
                          v-model:value="bot_to_add.content"
                          @init="editorInit"
                          theme="textmate"
                          style="height: 500px"
                          :options="{
                            enableBasicAutocompletion: true, //basic auto complete
                            enableSnippets: true, // code paragraph
                            enableLiveAutocompletion: true, // real-time auto complete
                            fontSize: 14, //
                            tabSize: 2, // 
                            showPrintMargin: false, // remove the codestyle column
                            highlightActiveLine: true,
                          }"
                        />
                      </div>
                    </div>
                    <div class="modal-footer">
                      <div class="error-message">{{ bot_to_add.error_message }}</div>
                      <button type="button" class="btn btn-primary" @click="add_new_bot">Submit</button>
                    </div>
                  </div>
                </div>
              </div>
              <!--end Modal-->
            </div>
            <div class="card-body">
              <table class="table table-hover">
                <thead>
                  <th> Bot Name</th>
                  <th> Create time</th>
                  <th> Operation</th>
                </thead>
                <tbody>
                  <tr v-for="bot in bots" :key="bot.id">
                    <td>{{ bot.botName }}</td>
                    <td>{{ bot.createTime }}</td>
                    <td>
                      <button class="btn btn-primary" data-bs-toggle="modal" :data-bs-target="'#modify-bot-btn-' + bot.id" style="margin-right: 10px;">Edit</button>
                      <!-- Modal -->
                      <div class="modal modal-xl modal-dialog-scrollable" :id="'modify-bot-btn-' + bot.id" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
                        <div class="modal-dialog">
                          <div class="modal-content">
                            <div class="modal-header">
                              <h1 class="modal-title fs-5" id="exampleModalLabel">Update bot</h1>
                              <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                            </div>
                            <div class="modal-body">
                              <!-- form -->
                              <div class="mb-3">
                                <label for="modify-bot-name" class="form-label">Update bot name</label>
                                <input v-model="bot.botName" type="text" class="form-control" id="modify-bot-name" :placeholder="'Before edit:\n' + bot.botName">
                              </div>
                              <div class="mb-3">
                                <label for="modify-bot-descrption" class="form-label">Update description</label>
                                <textarea v-model="bot.description" class="form-control" id="modify-bot-description" rows="3" :placeholder="'Before edit:\n'+ bot.description"></textarea>
                              </div>
                              <div class="mb-3">
                                <label for="modify-bot-descrption" class="form-label">Update code</label>
                                <VAceEditor
                                  v-model:value="bot.content"
                                  @init="editorInit"
                                  theme="textmate"
                                  style="height: 500px"
                                  :options="{
                                    enableBasicAutocompletion: true, //basic auto complete
                                    enableSnippets: true, // code paragraph
                                    enableLiveAutocompletion: true, // real-time auto complete
                                    fontSize: 14, //
                                    tabSize: 2, // 
                                    showPrintMargin: false, // remove the codestyle column
                                    highlightActiveLine: true,
                                  }"
                                />
                              </div>
                              <!-- end form -->
                            </div>
                            <div class="modal-footer">
                              <div class="error-message">{{ bot.error_message }}</div>
                              <button type="button" class="btn btn-primary" @click="modify_bot(bot)">Update</button>
                            </div>
                          </div>
                        </div>
                      </div> 
                      <!--end Modal-->
                      <button class="btn btn-danger" data-bs-toggle="button" @click="remove_bot(bot)">Delete</button></td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>
</template>
  
  
<script>
import { ref, reactive } from "vue"
import $ from "jquery"
import { useStore } from "vuex";
import { Modal } from "bootstrap/dist/js/bootstrap";
import { VAceEditor } from 'vue3-ace-editor';
import ace from 'ace-builds';


import 'ace-builds/src-noconflict/mode-json';
import 'ace-builds/src-noconflict/theme-chrome';
import 'ace-builds/src-noconflict/ext-language_tools';

export default {
  components: {
    VAceEditor,
  },
  setup() {
    ace.config.set(
    "basePath", 
    "https://cdn.jsdelivr.net/npm/ace-builds@" + require('ace-builds').version + "/src-noconflict/")

    const store = useStore();
    let bots = ref([]);

    const bot_to_add = reactive ({
      botName: "",
      description: "",
      content: "",
      error_message: "",
    })

    const refresh_bots = () => {
      $.ajax({
        url: "http://localhost:8888/user/bot/getall/",
        type: "GET",
        data: {
        },
        headers: {
          Authorization: "Bearer " + store.state.user.token
        },
        success(resp) {
          bots.value = resp;
        },
        error(resp) {
          console.log(resp);
        }
      })
    }

    refresh_bots();

    const add_new_bot = () => {
      bot_to_add.error_message = "";
      $.ajax({
        url: "http://localhost:8888/user/bot/add/",
        type: "POST",
        data: {
          botName: bot_to_add.botName,
          description: bot_to_add.description,
          content: bot_to_add.content,
        },
        headers: {
          Authorization: "Bearer " + store.state.user.token
        },
        success(resp) {
          if(resp.error_message === "success") {
            refresh_bots();
            bot_to_add.botName = "";
            bot_to_add.description = "";
            bot_to_add.content= "";
            Modal.getInstance("#add-bot-btn").hide();
          } else {
            bot_to_add.error_message = resp.error_message;
          }
        },
        error(resp) {
          console.log(resp);
        }
      })
    }

    const remove_bot = (bot) => {
      $.ajax({
        url: "http://localhost:8888/user/bot/remove/",
        type: "POST",
        data: {
          botId: bot.id,
        },
        headers: {
          Authorization: "Bearer " + store.state.user.token
        },
        success(resp) {
          if (resp.error_message === "success") {
            refresh_bots();
          }
        },
        error(resp) {
          console.log(resp);
        }
      })
    }

    const modify_bot = (bot) => {
      bot.error_message = "";
      $.ajax({
        url: "http://localhost:8888/user/bot/update/",
        type: "POST",
        data: {
          botId: bot.id,
          botName: bot.botName,
          description: bot.description,
          content: bot.content,
        },
        headers: {
          Authorization: "Bearer " + store.state.user.token
        },
        success(resp) {
          if(resp.error_message === "success") {
            refresh_bots();
            Modal.getInstance("#modify-bot-btn-" + bot.id).hide();
          } else {
            bot.error_message = resp.error_message;
          }
        },
        error(resp) {
          console.log(resp);
        }
      })
    }

    return {
      bots,
      bot_to_add,
      add_new_bot,
      remove_bot,
      modify_bot,
    }
  }
}
</script>

  
<style scoped>
div.error-message {
  color: red;
}
</style>