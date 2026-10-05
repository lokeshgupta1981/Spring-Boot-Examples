package com.howtodoinjava.logging;

import java.util.Properties;
import org.springframework.boot.ansi.AnsiOutput;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * Spring Boot copies logging.pattern.*, logging.threshold.* and logging.structured.* into
 * system properties (CONSOLE_LOG_PATTERN, ...) and never overwrites a value that is already set.
 * Several applications started in one test JVM would keep the first values, so each test
 * restores the original system properties and the ANSI setting.
 */
class RestoreSystemProperties implements BeforeEachCallback, AfterEachCallback {

  private Properties saved;

  @Override
  public void beforeEach(ExtensionContext context) {
    saved = new Properties();
    saved.putAll(System.getProperties());
  }

  @Override
  public void afterEach(ExtensionContext context) {
    System.setProperties(saved);
    // spring.output.ansi.enabled is stored in a static field
    AnsiOutput.setEnabled(AnsiOutput.Enabled.DETECT);
  }
}
