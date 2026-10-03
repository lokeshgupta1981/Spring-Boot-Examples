package com.howtodoinjava.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * Validates {@link OpenDay}: null is valid (combine with @NotNull), Mondays are not.
 */
public class OpenDayValidator implements ConstraintValidator<OpenDay, LocalDate> {

  @Override
  public boolean isValid(LocalDate date, ConstraintValidatorContext context) {
    return date == null || date.getDayOfWeek() != DayOfWeek.MONDAY;
  }
}
