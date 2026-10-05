package com.howtodoinjava.boot4;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/** Fake supplier API, so the HTTP service client has something to call when the app runs. */
@RestController
public class SupplierController {

  private final Map<String, Integer> cents = Map.of("apple", 120, "banana", 45);

  @GetMapping("/supplier/prices/{item}")
  public Price price(@PathVariable String item) {
    return new Price(item, cents.getOrDefault(item, 0));
  }
}
