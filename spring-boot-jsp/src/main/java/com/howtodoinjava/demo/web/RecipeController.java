package com.howtodoinjava.demo.web;

import com.howtodoinjava.demo.model.Recipe;
import com.howtodoinjava.demo.service.RecipeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RecipeController {

  private final RecipeService recipeService;

  public RecipeController(RecipeService recipeService) {
    this.recipeService = recipeService;
  }

  @GetMapping("/recipes")
  public String list(Model model) {
    model.addAttribute("recipes", recipeService.findAll());
    return "recipes";                 // renders /WEB-INF/jsp/recipes.jsp
  }

  @GetMapping("/recipes/new")
  public String form() {
    return "recipe-form";             // renders /WEB-INF/jsp/recipe-form.jsp
  }

  @PostMapping("/recipes")
  public String add(@RequestParam String name, @RequestParam int minutes) {
    recipeService.add(new Recipe(name.strip(), minutes));
    return "redirect:/recipes";       // browser loads the list again
  }
}
