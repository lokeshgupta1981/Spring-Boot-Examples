package com.howtodoinjava.demo.web.util;

public class TimeoutUtils {

  public static boolean busyOperation(long millis) {
    long startTime = System.currentTimeMillis();
    long targetTime = startTime + millis; // 10 seconds in milliseconds

    while (System.currentTimeMillis() < targetTime) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
    return true;
  }
}
