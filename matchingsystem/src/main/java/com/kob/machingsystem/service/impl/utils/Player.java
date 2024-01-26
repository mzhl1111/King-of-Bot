package com.kob.machingsystem.service.impl.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Player {
  private Integer userId;
  private Integer ranking;
  private Integer waitedTime;

}
