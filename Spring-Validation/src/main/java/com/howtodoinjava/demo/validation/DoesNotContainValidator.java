package com.howtodoinjava.demo.validation;

import com.howtodoinjava.demo.validation.annotation.DoesNotContain;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DoesNotContainValidator implements ConstraintValidator<DoesNotContain, String> {
  private String[] chars;

  @Override
  public void initialize(DoesNotContain constraintAnnotation) {
    this.chars = constraintAnnotation.chars();
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null) {
      return true;
    }
    for (String c : chars) {
      if (value.contains(c)) {
        return false;
      }
    }
    return true;
  }
}

