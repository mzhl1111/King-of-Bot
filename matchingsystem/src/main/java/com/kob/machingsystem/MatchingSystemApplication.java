package com.kob.machingsystem;

import com.kob.machingsystem.service.MatchingService;
import com.kob.machingsystem.service.impl.MatchingServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MatchingSystemApplication {

  public static void main(String[] args) {
    MatchingServiceImpl.matchingPool.start(); //start matching thread
    SpringApplication.run(MatchingSystemApplication.class, args);
  }
}
