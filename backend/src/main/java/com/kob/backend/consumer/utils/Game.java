package com.kob.backend.consumer.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.kob.backend.consumer.WebSocketServer;
import com.kob.backend.pojo.Replay;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;
import lombok.Getter;
import lombok.Setter;

public class Game extends Thread {
  private final Integer rows;
  private final Integer cols;
  private final Integer nWalls;

  private final int[][] g;
  private final static int[] dx = {-1, 0, 1, 0};
  private final static int[] dy = {0, 1, 0, -1};

  @Getter
  private final Player playerA, playerB;
  @Setter

  private Integer nextStepA = null; // state : null, 0 up, 1 down, 2 left, 3 right
  @Setter
  private Integer nextStepB = null; // state : null, 0 up, 1 down, 2 left, 3 right

  private ReentrantLock lock = new ReentrantLock();

  private String status = "Playing"; // one of "Playing" or "Finished"
  private String loser = ""; // one of "All", "A", "B"

  public Game(Integer rows, Integer cols, Integer nWalls, Integer idA, Integer idB) {
    this.rows = rows;
    this.cols = cols;
    this.nWalls = nWalls;
    this.g = new int[rows][cols];
    this.playerA = new Player(idA, this.rows - 2, 1, new ArrayList<>());
    this.playerB = new Player(idB, 1, this.cols - 2, new ArrayList<>());

  }

  public void secureSetNextA(Integer nextStepA) {
    lock.lock();
    try {
      this.setNextStepA(nextStepA);
    } finally {
      lock.unlock();
    }
  }

  public void secureSetNextB(Integer nextStepB) {
    lock.lock();
    try {
      this.setNextStepB(nextStepB);
    } finally {
      lock.unlock();
    }
  }

  public int[][] getGame() {
    return g;
  }

  private void clean() {
    for (int i = 0; i < this.rows; i++) {
      for (int j = 0; j < this.cols; j++) {
        this.g[i][j] = 0;
      }
    }
  }

  private boolean checkConnectivty(int sx, int sy, int tx, int ty) {
    if (sx == tx && sy == ty) {
      return true;
    }
    g[sx][sy] = 1;

    for (int i = 0; i < 4; i++) {
      int x = sx + dx[i];
      int y = sy + dy[i];
      if (x >= 0 && x < this.rows && y >= 0 && y < this.cols && g[x][y] == 0) {
        if (checkConnectivty(x, y, tx, ty)) {
          g[sx][sy] = 0;
          return true;
        }
      }
    }

    g[sx][sy] = 0;
    return false;

  }


  private boolean draw() {
    clean();

    for (int r = 0; r < this.rows; r++) {
      g[r][0] = g[r][this.cols - 1] = 1;
    }

    for (int c = 0; c < this.cols; c++) {
      g[0][c] = g[this.rows - 1][c] = 1;
    }

    Random random = new Random();
    for (int i = 0; i < this.nWalls / 2; i++) {
      for (int j = 0; j < 1000; j++) {
        int r = random.nextInt(this.rows);
        int c = random.nextInt(this.cols);

        if (g[r][c] == 1 || g[this.rows - 1 - r][this.cols - 1 - c] == 1) {
          continue;
        }
        if (r == this.rows - 2 && c == 1 || r == 1 && c == this.cols - 2) {
          continue;
        }

        g[r][c] = g[this.rows - 1 - r][this.cols - 1 - c] = 1;
        break;
      }
    }
    return checkConnectivty(this.rows - 2, 1, 1, this.cols - 2);
  }

  public void createMap() {
    for (int i = 0; i < 1000; i++) {
      if (draw()) {
        break;
      }
    }
  }

  private boolean check_valid(List<Cell> cellsA, List<Cell> cellsB) {
    int n = cellsA.size();
    Cell cell = cellsA.get(n -1); // head
    if(g[cell.x][cell.y] == 1) return false; // check if head hits the wall

    for(int i =0; i < n - 1; i++) { // check if head hits own body
      if(Objects.equals(cellsA.get(i).x, cell.x) && Objects.equals(cellsA.get(i).y, cell.y)) {
        return false;
      }
    }

    for(int i =0; i < n - 1; i++) {  // check if head his other's body
      if(Objects.equals(cellsB.get(i).x, cell.x) && Objects.equals(cellsB.get(i).y, cell.y)) {
        return false;
      }
    }

    return true;
  }

  private void judge() {  // judge if next step of the two player is legal
    List<Cell> cellsA = playerA.getCells();
    List<Cell> cellsB = playerB.getCells();

    boolean validA = check_valid(cellsA, cellsB);
    boolean validB = check_valid(cellsB, cellsA);
    if (!validA || !validB) {
      status = "finished";

      if (!validA && !validB) {
        loser = "All";
      } else if (!validA) {
        loser = "A";
      } else {
        loser = "B";
      }
    }
  }


  private boolean nextStep() { // wait for two plays to finish the next step
    System.out.println("nextstep");
    try {
      Thread.sleep(200);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

      for (int i = 0; i < 50; i++) {
        try {
          Thread.sleep(100);
          lock.lock();
          try{
            if (nextStepA != null && nextStepB != null) {
              playerA.getSteps().add(nextStepA);
              playerB.getSteps().add(nextStepB);
              System.out.printf("next step set success a  %d, b  %d \n", nextStepA, nextStepB);
              return true;
            }
          } finally {
            lock.unlock();
          }
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      return false;
  }


  private void broadcast(String message) {
    System.out.println("broadcast:" +  message);
    if (WebSocketServer.users.get(playerA.getId()) != null) {
      WebSocketServer.users.get(playerA.getId()).sendMessage(message);
    }
    if (WebSocketServer.users.get(playerB.getId()) != null) {
      WebSocketServer.users.get(playerB.getId()).sendMessage(message);
    }
  }

  private void broadcastMove() { // broad current game status to two player
    JSONObject resp = new JSONObject();
    lock.lock();
    try {
      resp.put("event", "move");
      resp.put("a_direction", nextStepA);
      resp.put("b_direction", nextStepB);
      nextStepA = nextStepB = null; // clean up
    } finally {
      lock.unlock();
    }

    broadcast(resp.toJSONString());
  }

  private void broadcastResult() { // send result to user
    JSONObject resp = new JSONObject();
    resp.put("event", "result");
    resp.put("loser", loser);

    saveReplayToDatabase();
    broadcast(resp.toJSONString());
  }

  private String jsonifyMap() {
    return JSON.toJSONString(g);
  }

  private void saveReplayToDatabase() {
    Replay replay = new Replay(
        null,
        playerA.getId(),
        playerA.getSx(),
        playerA.getSy(),
        playerB.getId(),
        playerB.getSx(),
        playerB.getSy(),
        playerA.jsonifySteps(),
        playerB.jsonifySteps(),
        jsonifyMap(),
        loser,
        new Date()
    );

    System.out.println(replay);

    WebSocketServer.replayMapper.insert(replay);
  }

  @Override
  public void run() {
    for (int i = 0; i < 1000; i++) {
      System.out.println(i);
      if (nextStep()) {
        judge();
        System.out.printf("Status: %s\n", status);
        if (status.equals("Playing")) {
          broadcastMove();
        } else {
          broadcastResult();
          break;
        }
      } else {
        status = "Finished";
        lock.lock();
        try{
          if (nextStepA == null && nextStepB == null) {
            loser = "All";
          } else if (nextStepA == null) {
            loser = "A";
          } else {
            loser = "B";
          }
        } finally {
          lock.unlock();
        }
        System.out.printf( "%d, %d, %s", this.nextStepA, this.nextStepB, this.status);
        broadcastResult();
        break;
      }
    }
  }
}
