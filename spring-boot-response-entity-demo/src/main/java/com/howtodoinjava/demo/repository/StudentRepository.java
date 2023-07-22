package com.howtodoinjava.demo.repository;

import com.howtodoinjava.demo.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    // Custom query methods if needed
}
