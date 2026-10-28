package com.howtodoinjava.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CatalogApplication {
  public static void main(String[] args) {
    var context = SpringApplication.run(CatalogApplication.class, args);
    System.out.println("catalogController registered = "
        + context.containsBean("catalogController"));
  }
}
