package com.howtodoinjava.demo.runner;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Log
public class CustomCommandLineRunner implements CommandLineRunner {

  @Value("${application.thread.pool.size:10}")
  private int threadPoolSize;

  @Override
  public void run(String... args) throws Exception {
    log.info("Thread pool size is : " + threadPoolSize);
  }
}
