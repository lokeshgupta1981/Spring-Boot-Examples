package com.howtodoinjava.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class App {

  @RestController
  public class FooController {

    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public String foo() {
      return "Response!";
    }
  }

  public static void main(String[] args) {
    SpringApplication.run(App.class, args);
  }

}
