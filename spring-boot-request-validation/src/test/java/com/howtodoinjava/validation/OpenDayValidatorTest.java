package com.howtodoinjava.validation;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

/**
 * The custom constraint, checked with a plain Jakarta Validator (no Spring context).
 */
class OpenDayValidatorTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void mondayIsRejected() {
    GroupBookingRequest request = new GroupBookingRequest("Morning crew",
        LocalDate.of(2026, 10, 5),
        new GroupBookingRequest.Contact("amit@example.com", "9876543210"),
        List.of(new GroupBookingRequest.Attendee("Amit", 35)));

    Set<ConstraintViolation<GroupBookingRequest>> violations = validator.validate(request);

    assertThat(violations).hasSize(1);
    ConstraintViolation<GroupBookingRequest> violation = violations.iterator().next();
    assertThat(violation.getPropertyPath()).hasToString("date");
    assertThat(violation.getMessage()).isEqualTo("studio is closed on Mondays");
  }

  @Test
  void tuesdayAndNullAreValid() {
    OpenDayValidator openDay = new OpenDayValidator();
    assertThat(openDay.isValid(LocalDate.of(2026, 10, 6), null)).isTrue();
    assertThat(openDay.isValid(null, null)).isTrue();
  }
}
