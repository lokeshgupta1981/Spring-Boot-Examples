package com.howtodoinjava.demo;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RecipeController {

  private final RecipeService recipeService;

  public RecipeController(RecipeService recipeService) {
    this.recipeService = recipeService;
  }

  // Open to everyone, see SecurityConfig: requestMatchers("/recipes").permitAll()
  @GetMapping("/recipes")
  public List<String> recipes() {
    return List.of("pasta", "salad", "soup");
  }

  // Needs a logged-in user: anyRequest().authenticated()
  @GetMapping("/recipes/mine")
  public List<String> myRecipes() {
    return List.of("omelette");
  }

  // Needs the ADMIN role: requestMatchers("/admin/**").hasRole("ADMIN")
  @GetMapping("/admin/recipes")
  public String adminRecipes() {
    return "all recipes of all users";
  }

  // The URL is open to any logged-in user; the service method checks the role
  @GetMapping("/recipes/secret")
  public String secretRecipe() {
    return recipeService.secretRecipe();
  }
}
