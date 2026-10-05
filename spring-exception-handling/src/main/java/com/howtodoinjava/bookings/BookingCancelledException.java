package com.howtodoinjava.bookings;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.GONE, reason = "The booking was cancelled")
public class BookingCancelledException extends RuntimeException {

  public BookingCancelledException(long id) {
    super("Booking " + id + " was cancelled");
  }
}
