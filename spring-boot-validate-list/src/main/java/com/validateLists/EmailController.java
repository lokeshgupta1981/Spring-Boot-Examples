/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validateLists;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/emails")
@Validated
public class EmailController {

    /* JSON example
[   
  {
    "email": "test@test.com"
  },
  {
    "email": "test.com"
  }
]
     */
    @PostMapping("/send")
    public ResponseEntity<String> sendEmails(@Valid @RequestBody List<@Valid EmailAddress> request, BindingResult result) {
        return ResponseEntity.ok("Emails sent successfully");
    }

    @PostMapping("/sendCustom")
    public ResponseEntity<String> sendEmailsCustom(@Valid @RequestBody List<@Valid EmailAddressForCustomValidator> request, BindingResult result) {
        return ResponseEntity.ok("Emails sent successfully");
    }

    //Validating Requests containing Object encapsulating a List
    /*
    {
  "emailAddresses": [
    {
      "email": "test@test.com"
    },
    {
      "email": "test@test.com"
    },
    {
      "email": "invalid-email"
    }
  ]
}
     */
    @PostMapping("/sendObject")
    public ResponseEntity<String> sendEmails1(@Valid @RequestBody EmailRequest request, BindingResult result) {
        return ResponseEntity.ok("Emails sent successfully");
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {

        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        Set<String> messages = new HashSet<>(constraintViolations.size());
        messages.addAll(constraintViolations.stream()
                .map(constraintViolation -> String.format("%s value '%s' %s", constraintViolation.getPropertyPath(),
                constraintViolation.getInvalidValue(), constraintViolation.getMessage()))
                .collect(Collectors.toList()));

        return new ResponseEntity<>("Your custom error message: " + messages, HttpStatus.BAD_REQUEST);
    }

}
