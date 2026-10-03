package com.howtodoinjava.validation;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Validation groups: different id rules for create and update.
 */
@RestController
public class YogaClassController {

  @PostMapping("/classes")
  @ResponseStatus(HttpStatus.CREATED)
  public YogaClassRequest create(@Validated(OnCreate.class) @RequestBody YogaClassRequest request) {
    return new YogaClassRequest(7L, request.title(), request.capacity());
  }

  @PutMapping("/classes")
  public YogaClassRequest update(@Validated(OnUpdate.class) @RequestBody YogaClassRequest request) {
    return request;
  }
}
