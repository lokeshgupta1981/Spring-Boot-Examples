package com.howtodoinjava.demo.customValidation;

import com.howtodoinjava.demo.validation.DoesNotContainValidator;
import com.howtodoinjava.demo.validation.annotation.DoesNotContain;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@SpringBootTest
public class DoesNotContainValidatorTest {

  private DoesNotContainValidator validator;
  private ConstraintValidatorContext context;

  @BeforeEach
  public void setUp() {
    validator = new DoesNotContainValidator();
    context = mock(ConstraintValidatorContext.class);
  }

  @Test
  public void testIsValidWithValidValue() {
    DoesNotContain doesNotContain = mock(DoesNotContain.class);
    when(doesNotContain.chars()).thenReturn(new String[]{"@", "#"});
    validator.initialize(doesNotContain);

    String value = "abcde";
    boolean result = validator.isValid(value, context);

    assertTrue(result);
  }

  @Test
  public void testIsValidWithInvalidValue() {
    DoesNotContain doesNotContain = mock(DoesNotContain.class);
    when(doesNotContain.chars()).thenReturn(new String[]{"@", "#"});
    validator.initialize(doesNotContain);

    String value = "abc@de";
    boolean result = validator.isValid(value, context);

    assertFalse(result);
  }

  @Test
  public void testIsValidWithNullValue() {
    DoesNotContain doesNotContain = mock(DoesNotContain.class);
    when(doesNotContain.chars()).thenReturn(new String[]{"@", "#"});
    validator.initialize(doesNotContain);

    String value = null;
    boolean result = validator.isValid(value, context);

    assertTrue(result);
  }

  @Test
  public void testIsValidWithEmptyValue() {
    DoesNotContain doesNotContain = mock(DoesNotContain.class);
    when(doesNotContain.chars()).thenReturn(new String[]{"@", "#"});
    validator.initialize(doesNotContain);

    String value = "";
    boolean result = validator.isValid(value, context);

    assertTrue(result);
  }
}
