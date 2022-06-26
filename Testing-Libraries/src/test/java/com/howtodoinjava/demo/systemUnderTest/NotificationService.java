package com.howtodoinjava.demo.systemUnderTest;

import lombok.extern.java.Log;

@Log
public class NotificationService {

  private static final NotificationService instance = new NotificationService();

  public static NotificationService getInstance() {
    return instance;
  }

  public static boolean sendNotification(String message) {
    log.info("Sending notification : " + message);
    return true;
  }
}
