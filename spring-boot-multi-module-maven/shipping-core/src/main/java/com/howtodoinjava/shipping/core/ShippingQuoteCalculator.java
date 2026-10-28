package com.howtodoinjava.shipping.core;
public class ShippingQuoteCalculator {
  public ShippingQuote quote(int weightKg) {
    if (weightKg < 1 || weightKg > 20) throw new IllegalArgumentException("weightKg must be between 1 and 20");
    return new ShippingQuote(weightKg, 40 + 12 * weightKg);
  }
}
