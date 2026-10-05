package com.howtodoinjava.security7;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.FactorGrantedAuthority;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityRulesTest {

  @Autowired
  MockMvcTester mvc;

  @Test
  void publicUrlIsOpen() {
    assertThat(mvc.get().uri("/public/currencies")).hasStatusOk();
  }

  @Test
  void loginRedirectIsRelative() {
    MvcTestResult result = mvc.get().uri("/expenses").accept(MediaType.TEXT_HTML).exchange();
    System.out.println("Anonymous GET /expenses -> " + result.getResponse().getStatus()
        + " Location: " + result.getResponse().getHeader(HttpHeaders.LOCATION));
    assertThat(result).hasStatus(HttpStatus.FOUND).hasHeader(HttpHeaders.LOCATION, "/login");
  }

  @Test
  void passwordLoginAddsFactorAuthority() {
    MvcTestResult result = mvc.get().uri("/expenses").with(httpBasic("lokesh", "secret")).exchange();
    System.out.println("Authorities after HTTP Basic: "
        + result.getRequest().getUserPrincipal());
    assertThat(result).hasStatusOk();
    assertThat(result).matches(authenticated().withAuthorities("ROLE_USER", "FACTOR_PASSWORD"));
  }

  @Test
  void deleteNeedsAdminRole() {
    assertThat(mvc.delete().uri("/expenses/taxi").with(user("lokesh").roles("USER")).with(csrf()))
        .hasStatus(HttpStatus.FORBIDDEN);
    assertThat(mvc.get().uri("/expenses").with(user("lokesh").roles("USER")))
        .hasStatusOk();
    assertThat(mvc.delete().uri("/expenses/taxi").with(user("anna").roles("ADMIN")).with(csrf()))
        .hasStatusOk();
  }

  @Test
  void reportNeedsSecondFactor() {
    MvcTestResult passwordOnly = mvc.get().uri("/reports/monthly")
        .accept(MediaType.TEXT_HTML)
        .with(httpBasic("anna", "secret")).exchange();
    System.out.println("anna with password only -> " + passwordOnly.getResponse().getStatus()
        + " Location: " + passwordOnly.getResponse().getHeader(HttpHeaders.LOCATION));
    assertThat(passwordOnly).hasStatus(HttpStatus.FOUND);

    MvcTestResult bothFactors = mvc.get().uri("/reports/monthly")
        .with(user("anna").authorities(
            () -> "ROLE_ADMIN",
            () -> FactorGrantedAuthority.PASSWORD_AUTHORITY,
            () -> FactorGrantedAuthority.OTT_AUTHORITY))
        .exchange();
    System.out.println("anna with password + ott -> " + bothFactors.getResponse().getStatus());
    assertThat(bothFactors).hasStatusOk();
  }

  @Test
  void auditNeedsAllRoles() {
    assertThat(mvc.get().uri("/audit/log").with(user("anna").roles("ADMIN")))
        .hasStatus(HttpStatus.FORBIDDEN);
    assertThat(mvc.get().uri("/audit/log").with(user("anna").roles("ADMIN", "AUDITOR")))
        .hasStatusOk();
  }

  @Test
  void spaCsrfUsesCookieAndHeader() throws Exception {
    MvcTestResult first = mvc.get().uri("/expenses").with(user("lokesh")).exchange();
    Cookie cookie = first.getResponse().getCookie("XSRF-TOKEN");
    System.out.println("Set-Cookie: " + first.getResponse().getHeader(HttpHeaders.SET_COOKIE));
    assertThat(cookie).isNotNull();

    assertThat(mvc.post().uri("/expenses").with(user("lokesh"))
        .contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"coffee\",\"amount\":3}"))
        .hasStatus(HttpStatus.FORBIDDEN);

    assertThat(mvc.post().uri("/expenses").with(user("lokesh"))
        .cookie(cookie).header("X-XSRF-TOKEN", cookie.getValue())
        .contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"coffee\",\"amount\":3}"))
        .hasStatus(HttpStatus.CREATED);
  }
}
