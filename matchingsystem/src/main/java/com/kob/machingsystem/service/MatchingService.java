package com.kob.machingsystem.service;

public interface MatchingService {
  String addPlayer(Integer userId, Integer ranking);
  String removePlayer(Integer userId);
}
