package com.howtodoinjava.demo.repository;

import com.howtodoinjava.demo.model.Recipe;
import org.springframework.data.repository.CrudRepository;

public interface RecipeRepository extends CrudRepository<Recipe, String> {
}
