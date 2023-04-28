package com.howtodoinjava.app.web;

import com.howtodoinjava.app.entity.Employee;
import com.howtodoinjava.app.service.EmployeeService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmployeeController {

  @Autowired
  private EmployeeService employeeService;

  @GetMapping("/employees")
  public ResponseEntity<List<Employee>> getAllEmployees() {

    return ResponseEntity.ok().body(employeeService.getAllEmployees());
  }
}
