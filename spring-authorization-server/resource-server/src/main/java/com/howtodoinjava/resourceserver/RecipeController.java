package com.howtodoinjava.resourceserver;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RecipeController {

  @GetMapping("/recipes")
  List<String> recipes() {
    return List.of("pancakes", "omelette");
  }

  @PostMapping("/recipes")
  @ResponseStatus(HttpStatus.CREATED)
  Map<String, String> addRecipe(@RequestBody String name, @AuthenticationPrincipal Jwt jwt) {
    return Map.of("added", name.strip(), "by", jwt.getSubject());
  }

  @GetMapping("/me")
  Map<String, Object> me(@AuthenticationPrincipal Jwt jwt) {
    List<String> roles = jwt.hasClaim("roles") ? jwt.getClaimAsStringList("roles") : List.of();
    return Map.of(
        "sub", jwt.getSubject(),
        "aud", jwt.getAudience(),
        "roles", roles);
  }
}
