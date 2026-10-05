package com.howtodoinjava.validation;

/**
 * Thrown when no booking exists for an id.
 */
public class BookingNotFoundException extends RuntimeException {

  public BookingNotFoundException(Long id) {
    super("Booking " + id + " not found");
  }
}
