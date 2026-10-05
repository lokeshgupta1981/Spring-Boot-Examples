package com.howtodoinjava.validation;

/**
 * A stored booking.
 */
public record Booking(Long id, String name, String email, Integer seats) {
}
