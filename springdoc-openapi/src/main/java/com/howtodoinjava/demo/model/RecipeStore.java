package com.howtodoinjava.demo.model;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

@Service
public class RecipeStore {

  private final Map<Long, Recipe> recipes = new ConcurrentHashMap<>();
  private final AtomicLong nextId = new AtomicLong(1);

  public RecipeStore() {
    save(new Recipe(null, "Pancakes", 20));
    save(new Recipe(null, "Tomato soup", 35));
  }

  public List<Recipe> findAll() {
    return recipes.values().stream().sorted((a, b) -> Long.compare(a.id(), b.id())).toList();
  }

  public Recipe findById(Long id) {
    Recipe recipe = recipes.get(id);
    if (recipe == null) {
      throw new RecipeNotFoundException(id);
    }
    return recipe;
  }

  public Recipe save(Recipe recipe) {
    Recipe saved = new Recipe(nextId.getAndIncrement(), recipe.name(), recipe.prepMinutes());
    recipes.put(saved.id(), saved);
    return saved;
  }

  public void delete(Long id) {
    findById(id);
    recipes.remove(id);
  }

  public int count() {
    return recipes.size();
  }
}
