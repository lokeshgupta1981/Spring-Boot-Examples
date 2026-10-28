package com.howtodoinjava.mockbean;

import org.springframework.stereotype.Service;

@Service
public class ShippingRateService {
  public int quoteInCents(String orderId) {
    return 499;
  }
}
