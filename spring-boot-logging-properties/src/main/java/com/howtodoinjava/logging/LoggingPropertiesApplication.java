package com.howtodoinjava.logging;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Playlist web application whose logging is configured only through application.properties.
 */
@SpringBootApplication
public class LoggingPropertiesApplication {

  public static void main(String[] args) {
    SpringApplication.run(LoggingPropertiesApplication.class, args);
  }
}
