package com.howtodoinjava.bookings;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Profile("!no-handlers")
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  // Same exception type as the local handler in BookingController
  @ExceptionHandler(BookingNotFoundException.class)
  public ProblemDetail handleNotFound(BookingNotFoundException ex) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    problem.setTitle("Not found (global handler)");
    return problem;
  }

  @ExceptionHandler(RoomUnavailableException.class)
  public ProblemDetail handleRoomUnavailable(RoomUnavailableException ex) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    problem.setTitle("Room unavailable");
    problem.setProperty("supportEmail", "desk@example.com");
    return problem;
  }

  // Catch-all: anything no other handler matched
  @ExceptionHandler(Exception.class)
  public ProblemDetail handleOther(Exception ex) throws Exception {
    if (ex.getClass().isAnnotationPresent(ResponseStatus.class)) {
      throw ex;   // let @ResponseStatus on the exception class decide
    }
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(
        HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong, please retry later");
    problem.setTitle("Unexpected error");
    return problem;
  }

  // Validation errors from @Valid @RequestBody
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
      HttpHeaders headers, HttpStatusCode status, WebRequest request) {

    Map<String, String> errors = new LinkedHashMap<>();
    for (FieldError error : ex.getBindingResult().getFieldErrors()) {
      errors.put(error.getField(), error.getDefaultMessage());
    }
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Request has invalid fields");
    problem.setTitle("Validation failed");
    problem.setProperty("errors", errors);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
  }
}
