package com.example.howtodoinjava.dao.Repository;

import com.example.howtodoinjava.Entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>  {


}

