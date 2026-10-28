package com.howtodoinjava.aop;

import org.springframework.stereotype.Service;

@Service
public class OrderService {
  @Measured
  public String checkout(String orderId) {
    validate(orderId);
    return "receipt:" + orderId;
  }

  @Measured
  public void validate(String orderId) {
    if (orderId == null || orderId.isBlank()) {
      throw new IllegalArgumentException("orderId is required");
    }
  }
}
