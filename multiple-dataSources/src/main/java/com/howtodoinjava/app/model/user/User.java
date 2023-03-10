package com.howtodoinjava.app.model.user;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tbl_users", schema = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @Column(name = "id")
  private Integer id;

  @Column(name = "first_name")
  private String firstName;

  @Column(name = "last_name")
  private String lastName;

  @Column(name = "email")
  private String email;
}
