package com.kob.backend.service.pk;

import org.springframework.context.annotation.Bean;

public interface StartGameService {
  String startGame(Integer aId, Integer bId);
}
