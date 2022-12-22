package com.howtodoinjava.app;

import com.howtodoinjava.app.service.SocialConnectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SocialConnectStarterSpringBootApplication implements CommandLineRunner {

  @Autowired
  SocialConnectService socialConnectService;

  public static void main(String[] args) {
    SpringApplication.run(SocialConnectStarterSpringBootApplication.class,
        args);
  }

  @Override
  public void run(String... args) {
    socialConnectService.fetchMessages().forEach(System.out::println);
  }
}