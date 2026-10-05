package com.howtodoinjava.excelimport.student;

import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StudentRepository extends JpaRepository<Student, Long> {

  @Query("select s.rollNumber from Student s where s.rollNumber in :rollNumbers")
  List<String> findExistingRollNumbers(Collection<String> rollNumbers);

  List<Student> findAllByOrderByRollNumber();
}
