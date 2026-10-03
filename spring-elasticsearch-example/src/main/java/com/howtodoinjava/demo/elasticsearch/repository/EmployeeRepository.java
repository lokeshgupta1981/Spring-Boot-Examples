package com.howtodoinjava.demo.elasticsearch.repository;

import com.howtodoinjava.demo.elasticsearch.entities.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface EmployeeRepository extends ElasticsearchRepository<Employee, String> {

    List<Employee> findByName(String name);
    List<Employee> findBySalaryBetween(Long startingSalary, Long endingSalary);

    @Query("{\"match\": {\"salary\": {\"query\": \"?0\"}}}")
    Page<Employee> findBySalary(Long salary, Pageable pageable);
}
