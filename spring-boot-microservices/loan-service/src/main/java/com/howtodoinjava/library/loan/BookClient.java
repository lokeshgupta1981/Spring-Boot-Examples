package com.howtodoinjava.library.loan;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange("/books")
public interface BookClient {

  @GetExchange("/{id}")
  Book getBook(@PathVariable String id);
}
