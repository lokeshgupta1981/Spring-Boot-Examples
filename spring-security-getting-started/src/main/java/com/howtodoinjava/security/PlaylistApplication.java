package com.howtodoinjava.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * A small playlist API secured with a custom {@code SecurityFilterChain} (see {@link SecurityConfig}).
 */
@SpringBootApplication
public class PlaylistApplication {

  public static void main(String[] args) {
    SpringApplication.run(PlaylistApplication.class, args);
  }
}
