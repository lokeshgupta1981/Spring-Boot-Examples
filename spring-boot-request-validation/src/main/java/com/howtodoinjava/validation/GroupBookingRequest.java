package com.howtodoinjava.validation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

/**
 * Booking for a group: a nested contact object and a list of attendees.
 */
public record GroupBookingRequest(
    @NotBlank String groupName,
    @NotNull @OpenDay LocalDate date,
    @NotNull @Valid Contact contact,
    @NotEmpty @Size(max = 4) List<@Valid Attendee> attendees) {

  public record Contact(
      @NotBlank @Email String email,
      @Pattern(regexp = "\\d{10}", message = "must be 10 digits") String phone) {
  }

  public record Attendee(
      @NotBlank String name,
      @NotNull @Min(12) Integer age) {
  }
}
