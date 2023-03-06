package com.howtodoinjava.demo.validation.annotation;


import com.howtodoinjava.demo.validation.DoesNotContainValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = DoesNotContainValidator.class)
public @interface DoesNotContain {

  String message() default "The field contains invalid characters";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  String[] chars();
}
