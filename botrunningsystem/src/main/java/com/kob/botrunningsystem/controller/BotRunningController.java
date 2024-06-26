package com.kob.botrunningsystem.controller;

import com.kob.botrunningsystem.servise.BotRunningService;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BotRunningController {
  @Autowired
  private BotRunningService botRunningService;

  @PostMapping("/bot/add")
  public String addBot(@RequestParam MultiValueMap<String, String> data) {
    Integer userId = Integer.parseInt(Objects.requireNonNull(data.getFirst("userId")));
    String botCode = data.getFirst("botCode");
    String input = data.getFirst("input");
    return botRunningService.addBot(userId, botCode, input);
  }
}
