package com.howtodoinjava.excelimport.student;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import java.time.LocalDate;

@Entity
public class Student {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @SequenceGenerator(sequenceName = "student_seq", allocationSize = 50)
  private Long id;

  @Column(unique = true, nullable = false)
  private String rollNumber;

  private String name;
  private String email;
  private String className;
  private Integer marks;
  private LocalDate admissionDate;

  protected Student() {
  }

  public Student(String rollNumber, String name, String email, String className,
      Integer marks, LocalDate admissionDate) {
    this.rollNumber = rollNumber;
    this.name = name;
    this.email = email;
    this.className = className;
    this.marks = marks;
    this.admissionDate = admissionDate;
  }

  public Long getId() { return id; }
  public String getRollNumber() { return rollNumber; }
  public String getName() { return name; }
  public String getEmail() { return email; }
  public String getClassName() { return className; }
  public Integer getMarks() { return marks; }
  public LocalDate getAdmissionDate() { return admissionDate; }
}
