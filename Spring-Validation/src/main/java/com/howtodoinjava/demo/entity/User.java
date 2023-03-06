package com.howtodoinjava.demo.entity;

import com.howtodoinjava.demo.validation.annotation.DoesNotContain;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class User {

  @DoesNotContain(chars = {"#", "%", "@"})
  private String username;
  private String email;
  private String password;
}
