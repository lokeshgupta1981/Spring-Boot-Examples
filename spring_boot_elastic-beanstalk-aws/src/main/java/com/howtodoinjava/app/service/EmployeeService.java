package com.howtodoinjava.app.service;

import com.howtodoinjava.app.entity.Employee;
import com.howtodoinjava.app.repo.EmployeeRepo;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class EmployeeService {

  @Autowired
  private EmployeeRepo employeeRepository;

  public List<Employee> getAllEmployees() {
    return employeeRepository.findAll();
  }
}
