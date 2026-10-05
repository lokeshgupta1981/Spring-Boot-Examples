package com.howtodoinjava.k8s;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class GreetingApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(GreetingApiApplication.class, args);
  }
}
