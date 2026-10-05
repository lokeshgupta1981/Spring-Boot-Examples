package com.howtodoinjava.demo.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

  @Autowired
  MockMvcTester mvc;

  @Test
  void publicUrlIsAllowedWithoutLogin() {
    mvc.get().uri("/recipes")
        .assertThat()
        .hasStatus(HttpStatus.OK)
        .bodyJson().isEqualTo("[\"pasta\",\"salad\",\"soup\"]");
  }

  @Test
  void protectedUrlIsDeniedWithoutLogin() {
    mvc.get().uri("/recipes/mine")
        .assertThat()
        .hasStatus(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void protectedUrlIsAllowedWithBasicAuth() {
    mvc.get().uri("/recipes/mine").with(httpBasic("ravi", "pass"))
        .assertThat()
        .hasStatus(HttpStatus.OK)
        .bodyJson().isEqualTo("[\"omelette\"]");
  }

  @Test
  void adminUrlIsForbiddenForUserRole() {
    mvc.get().uri("/admin/recipes").with(httpBasic("ravi", "pass"))
        .assertThat()
        .hasStatus(HttpStatus.FORBIDDEN);
  }

  @Test
  @WithMockUser(username = "admin", roles = "ADMIN")
  void adminUrlIsAllowedForAdminRole() {
    mvc.get().uri("/admin/recipes")
        .assertThat()
        .hasStatus(HttpStatus.OK)
        .hasBodyTextEqualTo("all recipes of all users");
  }

  @Test
  void methodSecurityBlocksUserRole() {
    mvc.get().uri("/recipes/secret").with(httpBasic("ravi", "pass"))
        .assertThat()
        .hasStatus(HttpStatus.FORBIDDEN);
  }

  @Test
  void methodSecurityAllowsAdminRole() {
    mvc.get().uri("/recipes/secret").with(httpBasic("admin", "pass"))
        .assertThat()
        .hasStatus(HttpStatus.OK)
        .hasBodyTextEqualTo("grandma's cake");
  }

  @Test
  void ignoredStaticPathSkipsSecurity() {
    // No filter runs for /css/**, so the request reaches MVC and gets 404, not 401
    mvc.get().uri("/css/site.css")
        .assertThat()
        .hasStatus(HttpStatus.NOT_FOUND);
  }
}
