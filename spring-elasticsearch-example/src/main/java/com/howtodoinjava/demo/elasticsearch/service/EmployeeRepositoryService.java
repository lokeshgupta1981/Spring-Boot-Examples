package com.howtodoinjava.demo.elasticsearch.service;


import com.howtodoinjava.demo.elasticsearch.entities.Employee;
import com.howtodoinjava.demo.elasticsearch.repository.EmployeeRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeRepositoryService {

    private final EmployeeRepository employeeRepository;

    public EmployeeRepositoryService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Employee createEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    public Employee updateEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    public void deleteEmployee(String employeeId) {
        employeeRepository.deleteById(employeeId);
    }

    public Employee getEmployee(String employeeId) {
        Optional<Employee> employeeOptional = employeeRepository.findById(employeeId);
        return employeeOptional.orElse(null);
    }

    public List<Employee> searchEmployeeWithSalaryBetween(long startingSalary, long endingSalary) {
        return employeeRepository.findBySalaryBetween(startingSalary, endingSalary);
    }

    public List<Employee> searchSalaryQuery(Long salary) {
        Pageable pageable = Pageable.ofSize(10);
        return employeeRepository.findBySalary(salary, pageable).getContent();
    }

    public  List<Employee> getEmployeeByName(String name) {
        return employeeRepository.findByName(name);
    }

}
