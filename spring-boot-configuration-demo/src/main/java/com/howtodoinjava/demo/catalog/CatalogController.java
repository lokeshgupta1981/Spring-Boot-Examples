package com.howtodoinjava.demo.catalog;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CatalogController {
  @GetMapping("/catalog/status")
  public String status() {
    return "Catalog ready";
  }
}
