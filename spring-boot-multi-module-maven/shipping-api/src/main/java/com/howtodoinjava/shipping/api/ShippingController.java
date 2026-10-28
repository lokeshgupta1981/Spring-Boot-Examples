package com.howtodoinjava.shipping.api;
import com.howtodoinjava.shipping.core.ShippingQuote;
import com.howtodoinjava.shipping.core.ShippingQuoteCalculator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class ShippingController {
  private final ShippingQuoteCalculator calculator;
  public ShippingController(ShippingQuoteCalculator calculator) { this.calculator = calculator; }
  @GetMapping("/shipping/quote")
  public ShippingQuote quote(@RequestParam int weightKg) { return calculator.quote(weightKg); }
}
