package com.example.spring_boot_elasticbeanstalkaws.service;

import com.example.spring_boot_elasticbeanstalkaws.entity.Employee;
import com.example.spring_boot_elasticbeanstalkaws.repo.EmployeeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class EmployeeService {
    @Autowired
    private EmployeeRepo employeeRepository;

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }
}
