package com.howtodoinjava.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Custom constraint: the date must be a day the studio is open (not a Monday).
 */
@Documented
@Constraint(validatedBy = OpenDayValidator.class)
@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
public @interface OpenDay {

  String message() default "studio is closed on Mondays";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
