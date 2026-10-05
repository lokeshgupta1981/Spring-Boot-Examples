package com.howtodoinjava.authserver;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

/** "jdbc" profile: clients and issued authorizations are stored in database tables. */
@Configuration(proxyBeanMethods = false)
@Profile("jdbc")
public class JdbcClientConfig {

  @Bean
  RegisteredClientRepository registeredClientRepository(JdbcOperations jdbcOperations) {
    return new JdbcRegisteredClientRepository(jdbcOperations);
  }

  @Bean
  OAuth2AuthorizationService authorizationService(JdbcOperations jdbcOperations,
      RegisteredClientRepository clients) {
    return new JdbcOAuth2AuthorizationService(jdbcOperations, clients);
  }

  @Bean
  ApplicationRunner seedClients(RegisteredClientRepository clients, PasswordEncoder encoder) {
    return args -> {
      saveIfMissing(clients, RecipeClients.recipeCli(encoder));
      saveIfMissing(clients, RecipeClients.recipeWeb());
    };
  }

  private static void saveIfMissing(RegisteredClientRepository clients, RegisteredClient client) {
    if (clients.findByClientId(client.getClientId()) == null) {
      clients.save(client);
    }
  }
}
