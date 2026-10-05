package com.howtodoinjava.bookings;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record BookingRequest(
    @NotBlank(message = "guest name is required") String guest,
    @Min(value = 1, message = "nights must be at least 1")
    @Max(value = 30, message = "nights must be at most 30") int nights) {
}
