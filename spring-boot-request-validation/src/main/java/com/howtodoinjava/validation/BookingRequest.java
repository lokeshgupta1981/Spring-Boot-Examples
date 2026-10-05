package com.howtodoinjava.validation;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request body for booking seats in a yoga class.
 */
public record BookingRequest(
    @NotBlank @Size(max = 40) String name,
    @NotBlank @Email String email,
    @NotNull @Min(1) @Max(4) Integer seats) {
}
