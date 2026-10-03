package com.howtodoinjava.nodatabase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.core.io.DefaultResourceLoader;

/** Starts a real application without spring.datasource.url and without an embedded database driver. */
@ExtendWith(OutputCaptureExtension.class)
class MissingUrlStartupTest {

  @SpringBootApplication
  static class NoDatabaseApp {
  }

  @Test
  void failureAnalyzerExplainsMissingUrl(CapturedOutput output) {
    SpringApplication app = new SpringApplication(
        new DefaultResourceLoader(new FilteredClassLoader("org.h2", "org.hsqldb", "org.apache.derby")),
        NoDatabaseApp.class);
    app.setDefaultProperties(java.util.Map.of("spring.config.name", "none", "spring.main.banner-mode", "off"));
    assertThatThrownBy(app::run).isNotNull();
    assertThat(output).contains(
        "Failed to configure a DataSource: 'url' attribute is not specified and no embedded datasource could be configured.");
    assertThat(output).contains("Reason: Failed to determine a suitable driver class");
  }
}
