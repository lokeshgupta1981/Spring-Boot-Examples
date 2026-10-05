package com.howtodoinjava.validation;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Nested object, list and custom constraint validation.
 */
@RestController
public class GroupBookingController {

  @PostMapping("/group-bookings")
  @ResponseStatus(HttpStatus.CREATED)
  public GroupBookingRequest create(@Valid @RequestBody GroupBookingRequest request) {
    return request;
  }
}
