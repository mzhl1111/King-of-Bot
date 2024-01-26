package com.kob.machingsystem.service.impl;

import com.kob.machingsystem.service.impl.utils.MatchingPool;
import org.springframework.stereotype.Service;
import com.kob.machingsystem.service.MatchingService;

@Service
public class MatchingServiceImpl implements MatchingService {
  public final static MatchingPool matchingPool = new MatchingPool();

  @Override
  public String addPlayer(Integer userId, Integer ranking) {
    System.out.println("add player" + userId + "  " + ranking);
    matchingPool.addPlayer(userId, ranking);
    return "add player success";
  }

  @Override
  public String removePlayer(Integer userId) {
    System.out.println("remove player" + userId);
    matchingPool.removePlayer(userId);
    return "remove player success";
  }
}
