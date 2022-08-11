package com.howtodoinjava.app.config;

import java.util.List;

public class SocialConnection {
  public SocialConnection() {
  }

  public SocialConnection connect(String key, String secret) {
    System.out.println("Initializing connection to social media...");
    return this;
  }

  public List<String> retrieve() {
    return List.of("Hello, World!");
  }
}
