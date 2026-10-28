package com.howtodoinjava.mockbean;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShippingController {
  private final ShippingRateService rates;

  public ShippingController(ShippingRateService rates) {
    this.rates = rates;
  }

  @GetMapping("/shipping/{orderId}")
  public Quote quote(@PathVariable String orderId) {
    return new Quote(orderId, rates.quoteInCents(orderId));
  }

  public record Quote(String orderId, int cents) {}
}
