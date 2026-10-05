package com.howtodoinjava.boot4;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.resilience.annotation.EnableResilientMethods;
import org.springframework.web.service.registry.ImportHttpServices;

@SpringBootApplication
@EnableResilientMethods
@ImportHttpServices(group = "prices", types = PriceClient.class)
public class Boot4Application {

  public static void main(String[] args) {
    SpringApplication.run(Boot4Application.class, args);
  }
}
