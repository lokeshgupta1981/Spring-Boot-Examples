package com.howtodoinjava.app.config;

public class SocialConnection {
  public SocialConnection() {
  }

  public SocialConnection connect(String key, String secret) {
    System.out.println("Initializing connection to social media with " + key + " and " + secret);
    return this;
  }
}
