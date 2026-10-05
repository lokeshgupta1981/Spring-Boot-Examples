package com.howtodoinjava.authserver;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

@Configuration(proxyBeanMethods = false)
public class TokenConfig {

  /** Adds the user's roles to access tokens issued for a logged-in user. */
  @Bean
  OAuth2TokenCustomizer<JwtEncodingContext> rolesClaimCustomizer() {
    return context -> {
      boolean accessToken = OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType());
      boolean userGrant = AuthorizationGrantType.AUTHORIZATION_CODE
          .equals(context.getAuthorizationGrantType());
      if (!accessToken || !userGrant) {
        return;   // client_credentials tokens have no user, so no roles
      }
      Authentication user = context.getPrincipal();
      Set<String> roles = AuthorityUtils.authorityListToSet(user.getAuthorities()).stream()
          .filter(authority -> authority.startsWith("ROLE_"))
          .map(authority -> authority.substring("ROLE_".length()))
          .collect(Collectors.toSet());
      context.getClaims().claim("roles", roles);
    };
  }
}
