package com.howtodoinjava.shipping.core;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;
class ShippingQuoteCalculatorTest {
  private final ShippingQuoteCalculator calculator = new ShippingQuoteCalculator();
  @Test void calculatesFee() { assertThat(calculator.quote(3)).isEqualTo(new ShippingQuote(3, 76)); }
  @Test void rejectsOutOfRangeWeight() { assertThatThrownBy(() -> calculator.quote(0)).isInstanceOf(IllegalArgumentException.class).hasMessage("weightKg must be between 1 and 20"); }
}
