package com.kob.backend.consumer;

import com.alibaba.fastjson2.JSONObject;
import com.kob.backend.consumer.utils.Game;
import com.kob.backend.consumer.utils.JwtAuthentication;
import com.kob.backend.mapper.ReplayMapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import com.sun.org.apache.xpath.internal.operations.Mult;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
@ServerEndpoint("/websocket/{token}")
public class WebSocketServer {

  private Session session = null;
  private User user;
  final public static ConcurrentHashMap<Integer, WebSocketServer> users =
      new ConcurrentHashMap<>();
  private static UserMapper userMapper;
  public static ReplayMapper replayMapper;
  private static RestTemplate restTemplate;
  private Game game = null;
  private final static String addPlayerUrl = "http://192.168.1.103:8889/player/add/";
  private final static String removePlayerUrl = "http://192.168.1.103:8889/player/remove/";

  @Autowired
  public void setUserMapper(UserMapper userMapper) {
    WebSocketServer.userMapper= userMapper;
  }

  @Autowired
  public void setReplayMapper(ReplayMapper replayMapper) {
    WebSocketServer.replayMapper = replayMapper;
  }

  @Autowired
  public void setRestTemplate(RestTemplate restTemplate) {
    WebSocketServer.restTemplate = restTemplate;
  }

  @OnOpen
  public void OnOpen(Session session, @PathParam("token") String token) {
    // Setup connection.
    this.session = session;
    System.out.println("Connected!");
    Integer userId = JwtAuthentication.getUserId(token);
    this.user = userMapper.selectById(userId);
    users.put(userId, this);

    if (this.user != null) {
      users.put(userId, this);
      System.out.printf("Connected User %d \n", userId);
    } else {
      try {
        this.session.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  @OnClose
  public void OnClose() {
    System.out.println("Connection closed");
    if (this.user != null) {
      users.remove(this.user.getId());
    }
  }

  public static void startGame(Integer aId, Integer bId) {
    User a = userMapper.selectById(aId);
    User b = userMapper.selectById(bId);

    Game game = new Game(13, 14, 20, a.getId(), b.getId());
    game.createMap();
    game.start();

    if (users.get(a.getId()) != null) users.get(a.getId()).game = game;
    if (users.get(b.getId()) != null) users.get(b.getId()).game = game;

    JSONObject respGame = new JSONObject();
    respGame.put("a_id", game.getPlayerA().getId());
    respGame.put("a_sx", game.getPlayerA().getSx());
    respGame.put("a_sy", game.getPlayerA().getSy());
    respGame.put("b_id", game.getPlayerB().getId());
    respGame.put("b_sx", game.getPlayerB().getSx());
    respGame.put("b_sy", game.getPlayerB().getSy());
    respGame.put("map", game.getGame());

    JSONObject respA = new JSONObject();
    respA.put("event", "start-matching");
    respA.put("opponent_username", b.getUsername());
    respA.put("opponent_photo", b.getPhoto());
    respA.put("game", respGame);
    if (users.get(a.getId()) != null) users.get(a.getId()).sendMessage(respA.toJSONString());


    JSONObject respB = new JSONObject();
    respB.put("event", "start-matching");
    respB.put("opponent_username", a.getUsername());
    respB.put("opponent_photo", a.getPhoto());
    respB.put("game", respGame);
    if (users.get(b.getId()) != null) users.get(b.getId()).sendMessage(respB.toJSONString());
  }

  private void startMatching() {
    System.out.println("start matching");
    MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
    data.add("userId", this.user.getId().toString());
    data.add("ranking", this.user.getRanking().toString());
    restTemplate.postForObject(addPlayerUrl, data, String.class);
  }

  private void stopMatching() {
    System.out.println("stop matching");
    MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
    data.add("userId", this.user.getId().toString());
    restTemplate.postForObject(removePlayerUrl, data, String.class);
  }

  private void move(int d) {
    System.out.printf("%d, %d, %d, %d \n", game.getPlayerA().getId(), game.getPlayerB().getId(),
        user.getId(), d);
    if (game.getPlayerA().getId().equals(user.getId())) {
      game.secureSetNextA(d);
    } else if (game.getPlayerB().getId().equals(user.getId())) {
      game.secureSetNextB(d);
    }
  }

  @OnMessage
  public void OnMessage(String message, Session session) {
    // Receive message from client.
    System.out.println("Message received");
    JSONObject data = JSONObject.parseObject(message);
    String event = data.getString("event");
    System.out.println(data);
    if ("start matching".equals(event)) {
      startMatching();
    } else if ("stop matching".equals(event)) {
    } else if ("move".equals(event)) {
      move(data.getInteger("direction"));
    }
  }

  public void sendMessage(String message) {
    synchronized (this.session) {
      try {
        this.session.getBasicRemote().sendText(message);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
