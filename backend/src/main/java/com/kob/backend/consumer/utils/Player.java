package com.kob.backend.consumer.utils;


import com.alibaba.fastjson2.JSON;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Player {
  private Integer id;
  private Integer botId; // -1 means play as human, other positive number as bot id;
  private String botCode;
  private Integer sx;
  private Integer sy;
  private List<Integer> steps;

  private boolean checkTailIncreasing(int steps) {
    if (steps <= 10) return true;
    return steps % 3 == 1;
  }

  public List<Cell> getCells() {
    List<Cell> res = new ArrayList<>();

    int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};
    int rounds = 0;
    int x = sx, y = sy;
    res.add(new Cell(x, y));
    for (int d: steps) {
      x += dx[d];
      y += dy[d];
      res.add(new Cell(x, y));
      if (! checkTailIncreasing( ++ rounds)) {
        res.remove(0);
      }
    }
    return res;
  }

  public String jsonifySteps() {
    return JSON.toJSONString(steps);
  }
}
