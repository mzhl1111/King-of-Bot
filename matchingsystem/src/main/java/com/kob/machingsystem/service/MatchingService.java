package com.kob.machingsystem.service;

public interface MatchingService {
  String addPlayer(Integer userId, Integer ranking, Integer botId);
  String removePlayer(Integer userId);
}
