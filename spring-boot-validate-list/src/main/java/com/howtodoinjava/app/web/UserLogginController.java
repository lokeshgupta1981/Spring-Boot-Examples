/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.howtodoinjava.app.web;

import com.howtodoinjava.app.model.User;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
@Validated
public class UserLogginController {

  /**
   * JSON example { "username": "test", "password": "psw123" }
   */
  @PostMapping
  public ResponseEntity<String> login(@Valid @RequestBody User user) {
    return ResponseEntity.ok("User logged in successfully");
  }

}
