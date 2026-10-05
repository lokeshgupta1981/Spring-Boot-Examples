package com.howtodoinjava.validation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Booking endpoints with request body, path variable and request parameter validation.
 * No class-level @Validated: Spring MVC 6.1+ validates the parameter constraints itself.
 */
@RestController
@RequestMapping("/bookings")
public class BookingController {

  private final BookingService bookingService;

  public BookingController(BookingService bookingService) {
    this.bookingService = bookingService;
  }

  @PostMapping
  public ResponseEntity<Booking> create(@Valid @RequestBody BookingRequest request) {
    Booking booking = bookingService.create(request);
    return ResponseEntity.created(URI.create("/bookings/" + booking.id())).body(booking);
  }

  @PostMapping("/batch")
  public List<Booking> createAll(@RequestBody List<@Valid BookingRequest> requests) {
    return requests.stream().map(bookingService::create).toList();
  }

  @GetMapping("/{id}")
  public Booking one(@PathVariable @Min(1) Long id) {
    return bookingService.find(id);
  }

  @GetMapping
  public List<Booking> search(@RequestParam @NotBlank String name,
                              @RequestParam(defaultValue = "10") @Min(1) @Max(50) int limit) {
    return bookingService.search(name, limit);
  }

  @PutMapping("/{id}")
  public Booking update(@PathVariable @Min(1) Long id,
                        @Valid @RequestBody BookingRequest request) {
    return bookingService.update(id, request);
  }
}
