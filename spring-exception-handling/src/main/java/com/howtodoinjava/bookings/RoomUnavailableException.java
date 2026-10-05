package com.howtodoinjava.bookings;

public class RoomUnavailableException extends RuntimeException {

  public RoomUnavailableException(String guest) {
    super("No room is free for " + guest);
  }
}
