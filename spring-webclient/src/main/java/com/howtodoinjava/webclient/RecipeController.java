package com.howtodoinjava.webclient;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * The local REST API that the WebClient examples call. It keeps the recipes in memory.
 */
@RestController
@RequestMapping("/recipes")
public class RecipeController {

  private final Map<Long, Recipe> recipes = new ConcurrentHashMap<>();
  private final AtomicLong nextId = new AtomicLong(1);

  public RecipeController() {
    save(new Recipe(null, "Pancakes", 20));
    save(new Recipe(null, "Omelette", 10));
    save(new Recipe(null, "Lasagna", 90));
  }

  @GetMapping
  public List<Recipe> list(@RequestParam(defaultValue = "0") int maxMinutes) {
    return recipes.values().stream()
        .filter(r -> maxMinutes == 0 || r.minutes() <= maxMinutes)
        .sorted((a, b) -> Long.compare(a.id(), b.id()))
        .toList();
  }

  @GetMapping("/{id}")
  public ResponseEntity<Recipe> get(@PathVariable Long id) {
    Recipe recipe = recipes.get(id);
    return recipe == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(recipe);
  }

  @PostMapping
  public ResponseEntity<Recipe> create(@RequestBody Recipe recipe) {
    Recipe saved = save(recipe);
    return ResponseEntity.created(URI.create("/recipes/" + saved.id())).body(saved);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    return recipes.remove(id) == null
        ? ResponseEntity.notFound().build()
        : ResponseEntity.noContent().build();
  }

  private Recipe save(Recipe recipe) {
    Recipe saved = new Recipe(nextId.getAndIncrement(), recipe.name(), recipe.minutes());
    recipes.put(saved.id(), saved);
    return saved;
  }
}
