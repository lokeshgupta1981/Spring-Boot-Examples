package com.howtodoinjava.demo;

import com.howtodoinjava.demo.model.Employee;
import com.howtodoinjava.demo.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class App implements CommandLineRunner {
  public static void main(String[] args) {
    SpringApplication.run(App.class);
  }

  @Autowired
  EmployeeService employeeService;

  @Override
  public void run(String... args) throws Exception {

    employeeService.create(new Employee("Lokesh Gupta"));
    log.info("New Employee Created...");
  }
}
