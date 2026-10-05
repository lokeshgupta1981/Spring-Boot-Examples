package com.howtodoinjava.demo.web;

import com.howtodoinjava.demo.model.Recipe;
import com.howtodoinjava.demo.model.RecipeStore;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recipes")
@Tag(name = "Recipes", description = "Create, read and delete recipes")
public class RecipeController {

  private final RecipeStore store;

  public RecipeController(RecipeStore store) {
    this.store = store;
  }

  @GetMapping
  @Operation(summary = "List all recipes")
  public List<Recipe> findAll() {
    return store.findAll();
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get one recipe by id")
  @ApiResponse(responseCode = "200", description = "The recipe")
  @ApiResponse(responseCode = "404", description = "No recipe with this id", content = @Content)
  public Recipe findById(
      @Parameter(description = "Id of the recipe", example = "1") @PathVariable Long id) {
    return store.findById(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Create a recipe")
  @ApiResponse(responseCode = "201", description = "Created")
  @ApiResponse(responseCode = "400", description = "Name is blank or prepMinutes is below 1", content = @Content)
  public Recipe create(@Valid @RequestBody Recipe recipe) {
    return store.save(recipe);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Delete a recipe")
  public void delete(@PathVariable Long id) {
    store.delete(id);
  }
}
