package com.howtodoinjava.security7;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.jackson.SecurityJacksonModules;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import tools.jackson.databind.json.JsonMapper;

class ApiChangesTest {

  @Test
  void pathPatternRequestMatcher() {
    PathPatternRequestMatcher deleteExpense = PathPatternRequestMatcher.withDefaults()
        .matcher(HttpMethod.DELETE, "/expenses/{name}");

    boolean delete = deleteExpense.matches(new MockHttpServletRequest("DELETE", "/expenses/taxi"));
    boolean get = deleteExpense.matches(new MockHttpServletRequest("GET", "/expenses/taxi"));
    boolean nested = deleteExpense.matches(new MockHttpServletRequest("DELETE", "/expenses/taxi/1"));
    System.out.println("DELETE /expenses/taxi   -> " + delete);
    System.out.println("GET /expenses/taxi      -> " + get);
    System.out.println("DELETE /expenses/taxi/1 -> " + nested);

    assertThat(delete).isTrue();
    assertThat(get).isFalse();
    assertThat(nested).isFalse();
  }

  @Test
  void jackson3SecurityModules() {
    Authentication auth = UsernamePasswordAuthenticationToken.authenticated(
        "lokesh", null, AuthorityUtils.createAuthorityList("ROLE_USER"));

    JsonMapper mapper = JsonMapper.builder()
        .addModules(SecurityJacksonModules.getModules(getClass().getClassLoader()))
        .build();

    String json = mapper.writeValueAsString(auth);
    Authentication back = mapper.readValue(json, Authentication.class);
    System.out.println("JSON: " + json);
    System.out.println("Read back: " + back.getName() + " " + back.getAuthorities());

    assertThat(back.getName()).isEqualTo("lokesh");
    assertThat(back.isAuthenticated()).isTrue();
  }

  @Test
  void authenticationToBuilder() {
    Authentication auth = UsernamePasswordAuthenticationToken.authenticated(
        "lokesh", null, AuthorityUtils.createAuthorityList("ROLE_USER"));

    Authentication withAuditor = auth.toBuilder()
        .authorities(list -> list.add(new SimpleGrantedAuthority("ROLE_AUDITOR")))
        .build();

    List<String> names = withAuditor.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
    System.out.println("toBuilder authorities: " + names + " class " + withAuditor.getClass().getSimpleName());
    assertThat(names).containsExactlyInAnyOrder("ROLE_USER", "ROLE_AUDITOR");
    assertThat(auth.getAuthorities()).hasSize(1);
  }
}
