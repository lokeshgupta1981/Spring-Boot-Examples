package com.howtodoinjava.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App {
  public static void main(String[] args) {

    String[] arr1 = new String[]{"token1"};
    String[] arr2 = new String[]{"token1"};

    arr1.equals(arr2);

    SpringApplication.run(App.class, args);
  }
}
