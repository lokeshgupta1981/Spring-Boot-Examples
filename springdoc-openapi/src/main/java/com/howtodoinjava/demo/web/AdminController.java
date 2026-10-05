package com.howtodoinjava.demo.web;

import com.howtodoinjava.demo.model.RecipeStore;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@Tag(name = "Admin", description = "Operations for the support team")
public class AdminController {

  private final RecipeStore store;

  public AdminController(RecipeStore store) {
    this.store = store;
  }

  @GetMapping("/stats")
  @Operation(summary = "Number of stored recipes")
  public Map<String, Integer> stats() {
    return Map.of("recipes", store.count());
  }

  @Hidden
  @GetMapping("/ping")
  public String ping() {
    return "pong";
  }
}
