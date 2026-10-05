package com.howtodoinjava.demo;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class RecipeService {

  // Method security: works because of @EnableMethodSecurity on SecurityConfig
  @PreAuthorize("hasRole('ADMIN')")
  public String secretRecipe() {
    return "grandma's cake";
  }
}
