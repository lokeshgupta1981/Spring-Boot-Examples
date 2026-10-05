package com.howtodoinjava.bookings;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bookings")
public class BookingController {

  private final BookingService service;

  public BookingController(BookingService service) {
    this.service = service;
  }

  @GetMapping("/{id}")
  public Booking get(@PathVariable long id) {
    return service.find(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Booking create(@Valid @RequestBody BookingRequest request) {
    return service.create(request);
  }

  @PostMapping("/{id}/rebook")
  public Booking rebook(@PathVariable long id) {
    return service.rebook(id);
  }

  // Local handler: only exceptions thrown from this controller reach it
  @ExceptionHandler(BookingNotFoundException.class)
  public ProblemDetail handleNotFound(BookingNotFoundException ex) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    problem.setTitle("Booking not found");
    return problem;
  }

  // One handler for several exception types; the argument is a common supertype
  @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
  public ProblemDetail handleBadInput(RuntimeException ex) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    return problem;     // 400 for both exception types
  }
}
