package com.howtodoinjava.defaults;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * A test-only application with spring-boot-starter-security and NO security configuration of its own.
 * It shows what Spring Boot does by default. Run it with: mvn spring-boot:test-run
 */
@SpringBootApplication
public class DefaultSecurityApplication {

  public static void main(String[] args) {
    SpringApplication.run(DefaultSecurityApplication.class, args);
  }

  @RestController
  static class HelloController {

    @GetMapping("/hello")
    String hello() {
      return "Hello, Spring Security";
    }
  }
}
