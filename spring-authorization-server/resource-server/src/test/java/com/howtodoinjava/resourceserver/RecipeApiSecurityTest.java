package com.howtodoinjava.resourceserver;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.startsWith;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The jwt() post-processor puts a ready Jwt into the security context, so these tests
 * never call the authorization server.
 */
@SpringBootTest
@AutoConfigureMockMvc
class RecipeApiSecurityTest {

  @Autowired
  MockMvc mvc;

  @Test
  void noTokenGives401() throws Exception {
    mvc.perform(get("/recipes"))
        .andExpect(status().isUnauthorized())
        .andExpect(header().string("WWW-Authenticate", startsWith("Bearer")));
  }

  @Test
  void tokenWithScopeReadsRecipes() throws Exception {
    mvc.perform(get("/recipes")
            .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_recipes.read"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0]").value("pancakes"));
  }

  @Test
  void tokenWithoutScopeGives403() throws Exception {
    mvc.perform(get("/recipes")
            .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_openid"))))
        .andExpect(status().isForbidden());
  }

  @Test
  void rolesClaimIsMappedByTheConverter() throws Exception {
    // authorities(...) maps the claims with the same converter the app uses
    mvc.perform(post("/recipes")
            .with(jwt()
                .jwt(token -> token.subject("lokesh").claim("roles", List.of("CHEF")))
                .authorities(SecurityConfig.authoritiesConverter()))
            .content("waffles"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.added").value("waffles"))
        .andExpect(jsonPath("$.by").value("lokesh"));
  }

  @Test
  void clientTokenWithoutRoleCannotAddRecipes() throws Exception {
    mvc.perform(post("/recipes")
            .with(jwt()
                .jwt(token -> token.subject("recipe-cli").claim("scope", "recipes.read"))
                .authorities(SecurityConfig.authoritiesConverter()))
            .content("waffles"))
        .andExpect(status().isForbidden());
  }

  @Test
  void meShowsClientTokenWithoutRoles() throws Exception {
    mvc.perform(get("/me")
            .with(jwt().jwt(token -> token.subject("recipe-cli").audience(List.of("recipe-cli")))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.sub").value("recipe-cli"))
        .andExpect(jsonPath("$.aud[0]").value("recipe-cli"))
        .andExpect(jsonPath("$.roles").isEmpty());
  }
}
