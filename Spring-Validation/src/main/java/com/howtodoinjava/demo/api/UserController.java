package com.howtodoinjava.demo.api;

import com.howtodoinjava.demo.entity.User;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class UserController {

  @PostMapping("/validate")
  public ResponseEntity<?> validateUserInfo(
      @Valid @RequestBody User user,
      BindingResult bindingResult) {

    List<String> errors = null;

    if (bindingResult.hasErrors()) {

      errors = bindingResult.getAllErrors()
          .stream()
          .map(objectError -> objectError.getDefaultMessage())
          .collect(Collectors.toList());
    }
    if (errors != null && errors.size() > 0) {
      return ResponseEntity.badRequest().body(errors);
    }
    return ResponseEntity.ok().body(user);
  }
}
