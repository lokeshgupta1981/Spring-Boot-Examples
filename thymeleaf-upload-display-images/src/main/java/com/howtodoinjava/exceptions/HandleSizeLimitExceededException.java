package com.howtodoinjava.exceptions;

import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class HandleSizeLimitExceededException {

  @ExceptionHandler(SizeLimitExceededException.class)
  public String handle(Model model, SizeLimitExceededException exception) {

    model.addAttribute("message", "File size limit exceeded!");
    return "upload";
  }
}
