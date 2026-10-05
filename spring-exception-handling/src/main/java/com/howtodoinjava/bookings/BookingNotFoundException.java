package com.howtodoinjava.bookings;

public class BookingNotFoundException extends RuntimeException {

  public BookingNotFoundException(long id) {
    super("Booking " + id + " does not exist");
  }
}
