package com.howtodoinjava.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Employee {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String name;
  private String phone;
  private String email;
  private String position;
  @Column(length = 1000)
  private String bio;

  public Employee(String name, String phone, String email, String position) {
    this.name = name;
    this.phone = phone;
    this.email = email;
    this.position = position;
  }
}
