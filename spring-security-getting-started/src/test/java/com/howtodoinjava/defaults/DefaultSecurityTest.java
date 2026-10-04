package com.howtodoinjava.defaults;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import jakarta.servlet.Filter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.autoconfigure.SecurityProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.MockMvc;

/**
 * What spring-boot-starter-security does when the application declares no security beans.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(OutputCaptureExtension.class)
class DefaultSecurityTest {

  @Autowired MockMvc mvc;
  @Autowired SecurityProperties securityProperties;
  @Autowired FilterChainProxy filterChainProxy;

  @Test
  void logsGeneratedPasswordForUserNamedUser(CapturedOutput output) {
    String password = securityProperties.getUser().getPassword();
    assertThat(securityProperties.getUser().getName()).isEqualTo("user");
    assertThat(output).contains("Using generated security password: " + password);
    String line = output.getOut().lines().filter(l -> l.contains("Using generated security password")).findFirst().orElseThrow();
    System.out.println("LOG LINE> " + line);
  }

  @Test
  void restClientWithoutCredentialsGets401() throws Exception {
    mvc.perform(get("/hello"))
        .andExpect(status().isUnauthorized())
        .andExpect(header().string("WWW-Authenticate", "Basic realm=\"Realm\", charset=\"UTF-8\""));
  }

  @Test
  void browserWithoutCredentialsIsRedirectedToLoginPage() throws Exception {
    mvc.perform(get("/hello").accept(MediaType.TEXT_HTML))
        .andExpect(status().isFound())
        .andExpect(redirectedUrl("/login"));
  }

  @Test
  void generatedLoginPageIsServed() throws Exception {
    mvc.perform(get("/login"))
        .andExpect(status().isOk())
        .andExpect(content().string(org.hamcrest.Matchers.containsString("Please sign in")));
  }

  @Test
  void userWithGeneratedPasswordGets200AndSecurityHeaders() throws Exception {
    String password = securityProperties.getUser().getPassword();
    mvc.perform(get("/hello").with(httpBasic("user", password)))
        .andExpect(status().isOk())
        .andExpect(content().string("Hello, Spring Security"))
        .andExpect(header().string("X-Frame-Options", "DENY"))
        .andExpect(header().string("X-Content-Type-Options", "nosniff"))
        .andExpect(header().string("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate"));
  }

  @Test
  void wrongPasswordGets401() throws Exception {
    mvc.perform(get("/hello").with(httpBasic("user", "wrong")))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void printsDefaultFilters() {
    List<Filter> filters = filterChainProxy.getFilterChains().getFirst().getFilters();
    filters.forEach(f -> System.out.println("DEFAULT FILTER> " + f.getClass().getSimpleName()));
    assertThat(filters).extracting(f -> f.getClass().getSimpleName())
        .contains("UsernamePasswordAuthenticationFilter", "BasicAuthenticationFilter", "AuthorizationFilter");
  }
}
