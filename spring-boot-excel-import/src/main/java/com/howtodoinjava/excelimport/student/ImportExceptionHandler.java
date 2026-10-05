package com.howtodoinjava.excelimport.student;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class ImportExceptionHandler {

  @ExceptionHandler(InvalidFileException.class)
  ProblemDetail invalidFile(InvalidFileException e) {
    return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
  }

  @ExceptionHandler(MaxUploadSizeExceededException.class)
  ProblemDetail tooLarge(MaxUploadSizeExceededException e) {
    return ProblemDetail.forStatusAndDetail(HttpStatus.CONTENT_TOO_LARGE,
        "The file is larger than the 5MB upload limit");
  }
}
