package com.howtodoinjava.demo.dao;


import com.howtodoinjava.demo.model.Employee;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends ListCrudRepository<Employee, Long> {
}
