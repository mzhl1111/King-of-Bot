package com.kob.machingsystem.service.impl.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class MatchingPool extends Thread {
  private static List<Player> players = new ArrayList<>();
  private ReentrantLock lock = new ReentrantLock();
  private static RestTemplate restTemplate;
  private final static String startGameUrl = "http://192.168.1.103:8888/pk/start/game/";


  @Autowired
  public void setRestTemplate(RestTemplate restTemplate) {
    MatchingPool.restTemplate = restTemplate;
  }
  public void addPlayer(Integer userId, Integer ranking){
    lock.lock();
    try {
      players.add(new Player(userId, ranking, 0));
    } finally {
      lock.unlock();
    }
  }

  public void removePlayer(Integer userId) {
    lock.lock();
    try {
      List<Player> newPlayers = new ArrayList<>();
      for (Player player : players) {
        if (!player.getUserId().equals(userId)) {
          newPlayers.add(player);
        }
      }
      players = newPlayers;
    } finally {
      lock.unlock();
    }
  }

  private void increaseWaitedTime() {
    for (Player player: players) {
      player.setWaitedTime(player.getWaitedTime() + 1);
    }
  }

  private boolean matched(Player a, Player b) {
    int rakingDelta = Math.abs(a.getRanking() - b.getRanking());
    int waitingTime = Math.min(a.getWaitedTime(), b.getWaitedTime());
//    System.out.println("waiting " + rakingDelta + "  " + waitingTime * 10);
    return rakingDelta <= waitingTime * 10;
  }

  private void sendResult(Player a, Player b) {
//    System.out.println("send result" + a.toString() + "   " + b.toString());
    MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
    data.add("aId", a.getUserId().toString());
    data.add("bId", b.getUserId().toString());
    restTemplate.postForObject(startGameUrl, data, String.class);
  }

  private void tryMatch() {
//    System.out.println("match players" + players.toString());
    boolean[] used = new boolean[players.size()];
    for (int i = 0; i < players.size(); i++) {
      if (used[i]) continue;
      for(int j = i + 1; j < players.size(); j++) {
        if (used[j]) continue;
        Player a = players.get(i);
        Player b = players.get(j);
        if (matched(a, b)) {
          used[i] = used[j] = true;
          sendResult(a, b);
          break;
        }
      }
    }
    List<Player> newPlayers = new ArrayList<>();
    for (int i = 0; i < players.size(); i++) {
      if (!used[i]) {
        newPlayers.add(players.get(i));
      }
    }
    players = newPlayers;
  }

  @Override
  public void run() {
    while (true) {
      try {
        Thread.sleep(1000);
        lock.lock();
        try{
          increaseWaitedTime();
          tryMatch();
        } finally {
          lock.unlock();
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

}
