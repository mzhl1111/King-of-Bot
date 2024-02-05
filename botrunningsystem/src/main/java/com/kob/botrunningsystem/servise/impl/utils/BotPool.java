package com.kob.botrunningsystem.servise.impl.utils;

import com.kob.botrunningsystem.servise.impl.utils.Bot;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BotPool extends Thread{
  private final ReentrantLock lock = new ReentrantLock();
  private Condition condition = lock.newCondition();
  private Queue<Bot> bots = new LinkedList<>();

  public void addBot(Integer userId, String botCode, String input) {
    lock.lock();
    try {
      bots.add(new Bot(userId, botCode, input));
      condition.signalAll();
    } finally {
      lock.unlock();
    }
  }

  private void consume(Bot bot) {
    Consumer consumer = new Consumer();
    consumer.startTimeout(2000, bot);

  }

  @Override
  public void run() {
    while (true) {
      lock.lock();
      if (bots.isEmpty()) {
        try {
          condition.await(); // await contains unlock operation
        } catch (InterruptedException e) {
          e.printStackTrace();
          break;
        }
      } else {
        Bot bot = bots.remove();
        consume(bot);

      }
    }
  }
}
