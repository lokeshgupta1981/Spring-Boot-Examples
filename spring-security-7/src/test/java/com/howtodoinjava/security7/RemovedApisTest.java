package com.howtodoinjava.security7;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.core.authority.FactorGrantedAuthority;

class RemovedApisTest {

  @Test
  void oldRequestMatchersAreGone() {
    assertThatThrownBy(() -> Class.forName("org.springframework.security.web.util.matcher.AntPathRequestMatcher"))
        .isInstanceOf(ClassNotFoundException.class);
    assertThatThrownBy(() -> Class.forName("org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher"))
        .isInstanceOf(ClassNotFoundException.class);
  }

  @Test
  void accessApiIsNotInCore() {
    assertThatThrownBy(() -> Class.forName("org.springframework.security.access.AccessDecisionManager"))
        .isInstanceOf(ClassNotFoundException.class);
  }

  @Test
  void andAndAuthorizeRequestsAreGone() {
    boolean hasAnd = Arrays.stream(FormLoginConfigurer.class.getMethods())
        .map(Method::getName).anyMatch("and"::equals);
    boolean hasAuthorizeRequests = Arrays.stream(HttpSecurity.class.getMethods())
        .map(Method::getName).anyMatch("authorizeRequests"::equals);
    System.out.println("and() present: " + hasAnd + ", authorizeRequests() present: " + hasAuthorizeRequests);
    assertThat(hasAnd).isFalse();
    assertThat(hasAuthorizeRequests).isFalse();
  }

  @Test
  void factorAuthorityNames() {
    System.out.println(FactorGrantedAuthority.PASSWORD_AUTHORITY + " " + FactorGrantedAuthority.OTT_AUTHORITY
        + " " + FactorGrantedAuthority.WEBAUTHN_AUTHORITY);
    assertThat(FactorGrantedAuthority.PASSWORD_AUTHORITY).isEqualTo("FACTOR_PASSWORD");
    assertThat(FactorGrantedAuthority.OTT_AUTHORITY).isEqualTo("FACTOR_OTT");
    assertThat(FactorGrantedAuthority.WEBAUTHN_AUTHORITY).isEqualTo("FACTOR_WEBAUTHN");
  }
}
