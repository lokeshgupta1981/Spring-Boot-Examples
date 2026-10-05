package com.howtodoinjava.authserver;

import java.time.Duration;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

/**
 * The two clients of the example. Both the in-memory and the JDBC repository use them.
 */
public final class RecipeClients {

  private RecipeClients() {
  }

  /** A backend job that calls the recipe API with its own identity (client_credentials). */
  public static RegisteredClient recipeCli(PasswordEncoder encoder) {
    return RegisteredClient.withId(UUID.randomUUID().toString())
        .clientId("recipe-cli")
        .clientSecret(encoder.encode("secret"))
        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
        .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
        .scope("recipes.read")
        .tokenSettings(TokenSettings.builder()
            .accessTokenTimeToLive(Duration.ofMinutes(10))
            .build())
        .build();
  }

  /** A browser app (public client) that logs users in with authorization_code + PKCE. */
  public static RegisteredClient recipeWeb() {
    return RegisteredClient.withId(UUID.randomUUID().toString())
        .clientId("recipe-web")
        .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
        .redirectUri("http://127.0.0.1:8080/callback")
        .scope(OidcScopes.OPENID)
        .scope("recipes.read")
        .clientSettings(ClientSettings.builder()
            .requireProofKey(true)
            .requireAuthorizationConsent(false)
            .build())
        .build();
  }
}
