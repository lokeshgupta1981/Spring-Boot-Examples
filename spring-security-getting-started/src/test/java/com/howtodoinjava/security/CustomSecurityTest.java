package com.howtodoinjava.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import jakarta.servlet.Filter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.MockMvc;

/**
 * The custom SecurityFilterChain: public URLs, authenticated URLs, admin-only URLs, CSRF and form login.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(OutputCaptureExtension.class)
class CustomSecurityTest {

  @Autowired MockMvc mvc;
  @Autowired FilterChainProxy filterChainProxy;
  @Autowired PasswordEncoder passwordEncoder;

  @Test
  void publicUrlNeedsNoLogin() throws Exception {
    mvc.perform(get("/public/top-songs"))
        .andExpect(status().isOk())
        .andExpect(content().string("Top songs: Yellow, Halo, Hello"));
  }

  @Test
  void protectedUrlWithoutLoginGets401() throws Exception {
    mvc.perform(get("/playlists"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void userWithCorrectPasswordGets200() throws Exception {
    mvc.perform(get("/playlists").with(httpBasic("lokesh", "password")))
        .andExpect(status().isOk())
        .andExpect(content().string("Playlists of lokesh: Morning Run, Focus"));
  }

  @Test
  void wrongPasswordGets401() throws Exception {
    mvc.perform(get("/playlists").with(httpBasic("lokesh", "wrong")))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void userWithoutAdminRoleGets403OnAdminUrl() throws Exception {
    mvc.perform(get("/admin/report").with(httpBasic("lokesh", "password")))
        .andExpect(status().isForbidden());
  }

  @Test
  void adminGets200OnAdminUrl() throws Exception {
    mvc.perform(get("/admin/report").with(httpBasic("admin", "admin123")))
        .andExpect(status().isOk())
        .andExpect(content().string("Users: 2, Playlists: 4"));
  }

  @Test
  @WithMockUser(username = "lokesh", roles = "USER")
  void mockUserSkipsThePassword() throws Exception {
    mvc.perform(get("/playlists"))
        .andExpect(status().isOk())
        .andExpect(content().string("Playlists of lokesh: Morning Run, Focus"));
  }

  @Test
  void postWithoutCsrfTokenGets403() throws Exception {
    mvc.perform(post("/playlists").content("Road Trip").with(httpBasic("lokesh", "password")))
        .andExpect(status().isForbidden());
  }

  @Test
  void postWithCsrfTokenGets200() throws Exception {
    mvc.perform(post("/playlists").content("Road Trip").with(httpBasic("lokesh", "password")).with(csrf()))
        .andExpect(status().isOk())
        .andExpect(content().string("Created playlist 'Road Trip' for lokesh"));
  }

  @Test
  void formLoginSucceedsAndRedirectsHome() throws Exception {
    mvc.perform(formLogin().user("lokesh").password("password"))
        .andExpect(status().isFound())
        .andExpect(redirectedUrl("/"))
        .andExpect(authenticated().withUsername("lokesh").withRoles("USER"));
  }

  @Test
  void formLoginWithWrongPasswordRedirectsToLoginError() throws Exception {
    mvc.perform(formLogin().user("lokesh").password("wrong"))
        .andExpect(redirectedUrl("/login?error"))
        .andExpect(unauthenticated());
  }

  @Test
  void noGeneratedPasswordWhenWeDeclareUsers(CapturedOutput output) {
    assertThat(output).doesNotContain("Using generated security password");
  }

  @Test
  void passwordsAreStoredAsBcryptHashes() {
    String hash = passwordEncoder.encode("password");
    System.out.println("HASH> " + hash);
    assertThat(hash).startsWith("{bcrypt}$2a$10$");
    assertThat(passwordEncoder.matches("password", hash)).isTrue();
  }

  @Test
  void printsCustomFilters() {
    List<Filter> filters = filterChainProxy.getFilterChains().getFirst().getFilters();
    filters.forEach(f -> System.out.println("CUSTOM FILTER> " + f.getClass().getSimpleName()));
    assertThat(filters).extracting(f -> f.getClass().getSimpleName())
        .containsSubsequence("SecurityContextHolderFilter", "CsrfFilter", "LogoutFilter",
            "UsernamePasswordAuthenticationFilter", "BasicAuthenticationFilter",
            "ExceptionTranslationFilter", "AuthorizationFilter");
  }
}
