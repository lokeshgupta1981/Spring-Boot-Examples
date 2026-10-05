package com.howtodoinjava.bookings;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/guests")
public class GuestController {

  private final BookingService service;

  public GuestController(BookingService service) {
    this.service = service;
  }

  // Throws the same BookingNotFoundException, but this controller has no local handler
  @GetMapping("/{bookingId}/name")
  public String guestName(@PathVariable long bookingId) {
    return service.find(bookingId).guest();
  }
}
