package com.howtodoinjava.jackson.boot;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
public class IngredientController {

  @GetMapping("/ingredients/{name}")
  public Ingredient get(@PathVariable String name) {
    Ingredient ingredient = new Ingredient();
    ingredient.setName(name);
    ingredient.setGrams(250);
    ingredient.setAddedOn(LocalDate.of(2026, 10, 5));
    return ingredient;
  }

  @PostMapping("/ingredients")
  public Ingredient add(@RequestBody Ingredient ingredient) {
    return ingredient;
  }
}
