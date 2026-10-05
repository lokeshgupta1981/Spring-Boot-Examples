package com.howtodoinjava.authserver;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

@Configuration(proxyBeanMethods = false)
public class ClientConfig {

  @Bean
  PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  /** Default profile: clients are kept in memory and recreated on every start. */
  @Bean
  @Profile("!jdbc")
  RegisteredClientRepository registeredClientRepository(PasswordEncoder encoder) {
    return new InMemoryRegisteredClientRepository(
        RecipeClients.recipeCli(encoder),
        RecipeClients.recipeWeb());
  }
}
