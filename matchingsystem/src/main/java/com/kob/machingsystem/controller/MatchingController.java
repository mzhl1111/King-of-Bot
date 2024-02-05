package com.kob.machingsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.kob.machingsystem.service.MatchingService;

@RestController
public class MatchingController {
  @Autowired
  private MatchingService matchingService;

  @PostMapping("/player/add/")
  public String addPlayer(@RequestParam MultiValueMap<String, String> data) {
    Integer userId = Integer.parseInt(data.getFirst("userId"));
    Integer ranking = Integer.parseInt(data.getFirst("ranking"));
    Integer botId = Integer.parseInt(data.getFirst("botId"));
    return matchingService.addPlayer(userId, ranking, botId);
  }

  @PostMapping("/player/remove/")
  public String removePlayer(@RequestParam MultiValueMap<String, String> data) {
    Integer userId = Integer.parseInt(data.getFirst("userId"));
    return matchingService.removePlayer(userId);
  }
}
