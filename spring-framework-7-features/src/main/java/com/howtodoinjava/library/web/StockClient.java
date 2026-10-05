package com.howtodoinjava.library.web;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange("/stock")
public interface StockClient {

  @GetExchange("/{title}")
  int copies(@PathVariable String title);
}
