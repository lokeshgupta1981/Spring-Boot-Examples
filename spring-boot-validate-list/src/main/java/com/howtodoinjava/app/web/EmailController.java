/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.howtodoinjava.app.web;

import com.howtodoinjava.app.model.EmailAddress;
import com.howtodoinjava.app.model.EmailRequest;
import com.howtodoinjava.app.validator.EmailAddressForCustomValidator;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
  public ResponseEntity<String> sendEmails(@Valid @RequestBody List<@Valid EmailAddress> request,
      BindingResult result) {
    return ResponseEntity.ok("Emails sent successfully");
  }

  @PostMapping("/sendCustom")
  public ResponseEntity<String> sendEmailsCustom(
      @Valid @RequestBody List<@Valid EmailAddressForCustomValidator> request,
      BindingResult result) {
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
  public ResponseEntity<String> sendEmails1(@Valid @RequestBody EmailRequest request,
      BindingResult result) {
    return ResponseEntity.ok("Emails sent successfully");
  }
}
