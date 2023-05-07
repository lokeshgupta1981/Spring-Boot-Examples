package com.howtodoinjava.app.service;

import com.howtodoinjava.app.entity.Employee;
import com.howtodoinjava.app.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

  @Autowired
  private EmployeeRepository employeeRepository;

  public List<Employee> getAllEmployees() {
    return employeeRepository.findAll();
  }

  public Optional<Employee> getEmployeeById(Long id) {
    return employeeRepository.findById(id);
  }

  public Employee addEmployee(Employee employee) {
    return employeeRepository.save(employee);
  }

  public Optional<Employee> updateEmployee(Long id, Employee employee) {

    Optional<Employee> existingEmployee = employeeRepository.findById(id);
    if (existingEmployee.isPresent()) {

      Employee updatedEmployee = existingEmployee.get();
      updatedEmployee.setName(employee.getName());
      updatedEmployee.setPhone(employee.getPhone());
      updatedEmployee.setEmail(employee.getEmail());
      updatedEmployee.setPosition(employee.getPosition());
      updatedEmployee.setBio(employee.getBio());
      employeeRepository.save(updatedEmployee);
    }
    return existingEmployee;
  }

  public void deleteEmployee(Long id) {
    employeeRepository.deleteById(id);
  }
}

