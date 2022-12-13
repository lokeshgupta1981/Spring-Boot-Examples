package com.howtodoinjava.app;

import com.howtodoinjava.app.model.Employee;
import com.howtodoinjava.app.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App implements CommandLineRunner {

  @Autowired
  EmployeeService service;

  @Override
  public void run(String... args) throws Exception {

    Employee employee = new Employee(1l, "John", "Doe", "email@domain.com");
    service.saveEmployee(employee);

    System.out.println(service.getEmployeeById(1L));
    System.out.println(service.getEmployeeById(1L));
  }

  public static void main(String[] args) {
    SpringApplication.run(App.class, args);
  }
}
