package com.howtodoinjava.boot4;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange("/prices")
public interface PriceClient {

  @GetExchange("/{item}")
  Price price(@PathVariable String item);
}
