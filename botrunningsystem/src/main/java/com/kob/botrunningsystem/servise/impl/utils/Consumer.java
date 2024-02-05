package com.kob.botrunningsystem.servise.impl.utils;

import com.kob.botrunningsystem.utils.BotInterface;
import java.util.UUID;
import org.joor.Reflect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class Consumer extends Thread {
  private Bot bot;
  private static RestTemplate restTemplate;
  private final static String receiveBotMoveUrl = "http://192.168.1.103:8888/pk/receive/bot/move/";

  @Autowired
  public void setRestTemplate(RestTemplate restTemplate){
    Consumer.restTemplate = restTemplate;
  }
  public void startTimeout(long timeout, Bot bot) {
    this.bot = bot;
    this.start();

    try {
      this.join(timeout);//最多等待timout ms
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    this.interrupt();
  }

  private String addUid(String code, String uid) {
    int k = code.indexOf(" implements com.kob.botrunningsystem.utils.BotInterface");
    return code.substring(0, k) + uid + code.substring(k);
  }

  @Override
  public void run() { //TODO: change to docker
    UUID uuid = UUID.randomUUID();
    String uid = uuid.toString().substring(0, 8);
    BotInterface botInterface = Reflect.compile(
        "com.kob.botrunningsystem.utils.Bot" + uid,
        addUid(bot.getBotCode(), uid)).create().get();

    Integer direction = botInterface.nextMove(bot.getInput());
    System.out.println("move-direction" + bot.getUserId() + " " + direction);

    MultiValueMap<String ,String> data = new LinkedMultiValueMap<>();
    data.add("userId", bot.getUserId().toString());
    data.add("direction", direction.toString());
    restTemplate.postForObject(receiveBotMoveUrl, data, String.class);
  }
}
