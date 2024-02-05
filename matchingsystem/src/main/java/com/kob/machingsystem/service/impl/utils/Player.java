package com.kob.machingsystem.service.impl.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.parameters.P;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Player {
  private Integer userId;
  private Integer ranking;
  private Integer botId;
  private Integer waitedTime;

  public Player(Integer userId) {
    this.userId = userId;
    this.ranking = 1500;
    this.botId = -1;
    this.waitedTime = 0;
  }

  @Override
  public boolean equals(Object o) {
    // Fast path for pointer equality:
    if (this == o) { // backward compatibility with default equals
      return true;
    }

    // If o isn't the right class then it can't be equal:
    if (!(o instanceof Player)) {
      return false;
    }

    // The successful instanceof check means our cast will succeed:
    Player that = (Player) o;

    return this.userId.equals(that.getUserId());
  }
}
