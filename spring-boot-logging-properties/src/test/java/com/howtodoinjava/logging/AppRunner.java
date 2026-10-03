package com.howtodoinjava.logging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Starts the application with extra properties. Each start re-initializes Logback,
 * so every test sees exactly the logging configuration it passes in.
 */
final class AppRunner {

  static final String TEST_LOG_FILE = "logging.file.name=target/test-logs/playlist.log";

  private AppRunner() {
  }

  static ConfigurableApplicationContext start(String... properties) {
    return start(WebApplicationType.NONE, properties);
  }

  static ConfigurableApplicationContext start(WebApplicationType type, String... properties) {
    // Command-line arguments override application.properties
    List<String> args = new ArrayList<>();
    if (Arrays.stream(properties).noneMatch(p -> p.startsWith("logging.file.name="))) {
      args.add("--" + TEST_LOG_FILE);
    }
    for (String property : properties) {
      args.add("--" + property);
    }
    return new SpringApplicationBuilder(LoggingPropertiesApplication.class)
        .web(type)
        .run(args.toArray(String[]::new));
  }
}
