package com.howtodoinjava.boot4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;

@Service
public class PriceService {

  private static final Logger log = LoggerFactory.getLogger(PriceService.class);

  private final PriceClient priceClient;

  public PriceService(PriceClient priceClient) {
    this.priceClient = priceClient;
  }

  @Retryable(includes = HttpServerErrorException.class, maxRetries = 2, delay = 100)
  public Price priceOf(String item) {
    log.info("Asking supplier for the price of {}", item);
    return priceClient.price(item);
  }
}
