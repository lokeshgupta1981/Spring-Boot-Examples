package com.howtodoinjava.boot4;

import java.time.LocalDate;
import java.util.Map;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

@Service
public class PantryService {

  private final Map<String, PantryItem> items = Map.of(
      "apple", new PantryItem("apple", 5, LocalDate.of(2026, 10, 12)),
      "banana", new PantryItem("banana", 3, LocalDate.of(2026, 10, 8)));

  public @Nullable PantryItem find(String name) {
    return items.get(name);
  }
}
