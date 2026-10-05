package com.howtodoinjava.library.loan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.service.registry.ImportHttpServices;

@SpringBootApplication
@ImportHttpServices(group = "books", types = BookClient.class)
public class LoanServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(LoanServiceApplication.class, args);
  }
}
