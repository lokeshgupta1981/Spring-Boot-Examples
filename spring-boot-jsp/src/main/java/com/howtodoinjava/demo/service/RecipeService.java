package com.howtodoinjava.demo.service;

import com.howtodoinjava.demo.model.Recipe;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Service;

@Service
public class RecipeService {

  private final List<Recipe> recipes = new CopyOnWriteArrayList<>();

  public RecipeService() {
    recipes.add(new Recipe("Pancakes", 20));
    recipes.add(new Recipe("Tomato Soup", 35));
  }

  public List<Recipe> findAll() {
    return List.copyOf(recipes);
  }

  public void add(Recipe recipe) {
    recipes.add(recipe);
  }
}
