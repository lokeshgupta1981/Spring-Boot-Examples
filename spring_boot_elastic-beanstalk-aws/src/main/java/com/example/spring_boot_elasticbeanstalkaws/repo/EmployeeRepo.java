package com.example.spring_boot_elasticbeanstalkaws.repo;

import com.example.spring_boot_elasticbeanstalkaws.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepo extends JpaRepository<Employee, Long> {
}
