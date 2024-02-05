package com.kob.backend.controller.pk;

import com.kob.backend.service.pk.ReceiveBotMoveService;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class receiveBotMoveController {

  @Autowired
  private ReceiveBotMoveService receiveBotMoveService;

  @PostMapping("/pk/receive/bot/move/")
  public String receiveBotMove(@RequestParam MultiValueMap<String, String> data) {
    Integer userId = Integer.parseInt(Objects.requireNonNull(data.getFirst("userId")));
    Integer direction = Integer.parseInt(Objects.requireNonNull(data.getFirst("direction")));
    return receiveBotMoveService.receiveBotMove(userId, direction);
  }
}
