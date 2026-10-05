package com.howtodoinjava.boot4;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pantry")
public class PantryController {

  private final PantryService pantryService;
  private final PriceService priceService;

  public PantryController(PantryService pantryService, PriceService priceService) {
    this.pantryService = pantryService;
    this.priceService = priceService;
  }

  @GetMapping(path = "/{name}", version = "1")
  public PantryItemV1 getV1(@PathVariable String name) {
    return PantryItemV1.from(findOrThrow(name));
  }

  @GetMapping(path = "/{name}", version = "2")
  public PantryItem getV2(@PathVariable String name) {
    return findOrThrow(name);
  }

  @GetMapping("/{name}/price")
  public Price price(@PathVariable String name) {
    return priceService.priceOf(findOrThrow(name).name());
  }

  @GetMapping("/thread")
  public String thread() {
    return Thread.currentThread().toString();
  }

  private PantryItem findOrThrow(String name) {
    PantryItem item = pantryService.find(name);
    if (item == null) {
      throw new PantryNotFoundException(name);
    }
    return item;
  }
}
