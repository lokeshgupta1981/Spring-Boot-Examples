package com.example.searchinkeycloakwithspringboot.config;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

  @Bean
  public Keycloak keycloak() {
    return KeycloakBuilder.builder()
        .serverUrl("http://localhost:8080/")
        .realm("master")
        .grantType(OAuth2Constants.PASSWORD)
        .username("admin")
        .password("admin")
        .clientId("admin-cli")
        .build();
  }
}
