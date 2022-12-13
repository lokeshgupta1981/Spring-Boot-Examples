package com.howtodoinjava.app.service;

import com.howtodoinjava.app.dao.EmployeeRepository;
import com.howtodoinjava.app.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmployeeService {

  @Autowired
  EmployeeRepository repository;

  @Cacheable(value = "employees", key = "#id")
  public Optional<Employee> getEmployeeById(Long id) {
    return repository.findById(id);
  }

  @CachePut(cacheNames = "employees", key = "#employee.id")
  public Employee updateEmployee(Employee employee) {
    return repository.save(employee);
  }

  public void saveEmployee(Employee employee) {
    repository.save(employee);
  }
}
