package com.howtodoinjava.validation;

import java.util.Map;
import java.util.TreeMap;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.method.ParameterErrors;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Turns validation errors into a ProblemDetail (RFC 9457) body with an "errors" map
 * of field or parameter name to message.
 */
@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

  // @Valid @RequestBody failed, and no constraint sits directly on a method parameter
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex, HttpHeaders headers,
      HttpStatusCode status, WebRequest request) {

    Map<String, String> errors = new TreeMap<>();
    ex.getBindingResult().getFieldErrors()
        .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

    return handleExceptionInternal(ex, problem(ex.getBody(), errors), headers, status, request);
  }

  // A constraint directly on a parameter (@PathVariable @Min(1) Long id) failed
  @Override
  protected ResponseEntity<Object> handleHandlerMethodValidationException(
      HandlerMethodValidationException ex, HttpHeaders headers,
      HttpStatusCode status, WebRequest request) {

    Map<String, String> errors = new TreeMap<>();
    ex.getParameterValidationResults().forEach(result -> {
      if (result instanceof ParameterErrors bodyErrors) {          // @Valid object parameter
        Integer index = bodyErrors.getContainerIndex();             // set for List<@Valid ...> elements
        String prefix = (index == null) ? ""
            : result.getMethodParameter().getParameterName() + "[" + index + "].";
        bodyErrors.getFieldErrors()
            .forEach(error -> errors.put(prefix + error.getField(), error.getDefaultMessage()));
      } else {                                                       // simple parameter
        String name = result.getMethodParameter().getParameterName();
        result.getResolvableErrors()
            .forEach(error -> errors.put(name, error.getDefaultMessage()));
      }
    });

    return handleExceptionInternal(ex, problem(ex.getBody(), errors), headers, status, request);
  }

  @ExceptionHandler(BookingNotFoundException.class)
  public ProblemDetail notFound(BookingNotFoundException ex) {
    return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
  }

  private ProblemDetail problem(ProblemDetail body, Map<String, String> errors) {
    body.setDetail("Validation failed");
    body.setProperty("errors", errors);
    return body;
  }
}
