package com.example.spring_boot_elasticbeanstalkaws.web;

import com.example.spring_boot_elasticbeanstalkaws.entity.Employee;
import com.example.spring_boot_elasticbeanstalkaws.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService ;

    @GetMapping("/employees")
    public ResponseEntity<List<Employee>> getAllEmployees(){

        return ResponseEntity.ok().body( employeeService.getAllEmployees());
    }
}
