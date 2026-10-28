package com.howtodoinjava.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LoggingOrderNotifier implements OrderNotifier {
  private static final Logger log = LoggerFactory.getLogger(LoggingOrderNotifier.class);
  @Override public void orderCreated(Long orderId) {
    log.info("Created order {}", orderId);
  }
}
