package com.howtodoinjava.authserver;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.UriComponentsBuilder;

import com.jayway.jsonpath.JsonPath;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.hasKey;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthorizationServerEndpointsTest {

  @Autowired
  MockMvc mvc;

  @Autowired
  JwtDecoder jwtDecoder;

  @Test
  void clientCredentialsReturnsSignedJwt() throws Exception {
    MvcResult result = mvc.perform(post("/oauth2/token")
            .with(httpBasic("recipe-cli", "secret"))
            .param("grant_type", "client_credentials")
            .param("scope", "recipes.read"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token_type").value("Bearer"))
        .andExpect(jsonPath("$.scope").value("recipes.read"))
        .andExpect(jsonPath("$.expires_in").value(599))
        .andReturn();

    String token = JsonPath.read(result.getResponse().getContentAsString(), "$.access_token");
    Jwt jwt = jwtDecoder.decode(token);

    assertThat(jwt.getHeaders()).containsEntry("kid", KeyConfig.CURRENT_KEY_ID);
    assertThat(jwt.getSubject()).isEqualTo("recipe-cli");
    assertThat(jwt.getIssuer()).hasToString("http://localhost:9000");
    assertThat(jwt.getClaimAsStringList("scope")).containsExactly("recipes.read");
    assertThat(jwt.hasClaim("roles")).isFalse();
  }

  @Test
  void wrongClientSecretIsRejected() throws Exception {
    mvc.perform(post("/oauth2/token")
            .with(httpBasic("recipe-cli", "wrong"))
            .param("grant_type", "client_credentials"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error").value("invalid_client"));
  }

  @Test
  void unknownScopeIsRejected() throws Exception {
    mvc.perform(post("/oauth2/token")
            .with(httpBasic("recipe-cli", "secret"))
            .param("grant_type", "client_credentials")
            .param("scope", "recipes.delete"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("invalid_scope"));
  }

  @Test
  void discoveryDocumentListsEndpoints() throws Exception {
    mvc.perform(get("/.well-known/openid-configuration"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.issuer").value("http://localhost:9000"))
        .andExpect(jsonPath("$.token_endpoint").value("http://localhost:9000/oauth2/token"))
        .andExpect(jsonPath("$.jwks_uri").value("http://localhost:9000/oauth2/jwks"))
        .andExpect(jsonPath("$.authorization_endpoint")
            .value("http://localhost:9000/oauth2/authorize"));
  }

  @Test
  void jwksPublishesBothPublicKeys() throws Exception {
    mvc.perform(get("/oauth2/jwks"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.keys", hasSize(2)))
        .andExpect(jsonPath("$.keys[*].kid")
            .value(org.hamcrest.Matchers.containsInAnyOrder("recipe-key-2", "recipe-key-1")))
        .andExpect(jsonPath("$.keys[0]", not(hasKey("d"))));   // no private key part
  }

  @Test
  void authorizationCodeWithPkceIssuesTokenWithRoles() throws Exception {
    String verifier = "dBjftJeZ4CVP-mB92K27uhbUJU1p1r_wW1gFWFOEjXk";
    String challenge = s256(verifier);

    // 1. The user logs in on the form login page of the authorization server
    MvcResult login = mvc.perform(formLogin().user("lokesh").password("password"))
        .andExpect(status().is3xxRedirection())
        .andReturn();
    MockHttpSession session = (MockHttpSession) login.getRequest().getSession(false);

    // 2. The logged-in user is redirected back to the client with a code
    MvcResult authorize = mvc.perform(get("/oauth2/authorize")
            .session(session)
            .queryParam("response_type", "code")
            .queryParam("client_id", "recipe-web")
            .queryParam("redirect_uri", "http://127.0.0.1:8080/callback")
            .queryParam("scope", "openid recipes.read")
            .queryParam("state", "xyz")
            .queryParam("code_challenge", challenge)
            .queryParam("code_challenge_method", "S256"))
        .andExpect(status().is3xxRedirection())
        .andReturn();

    String location = authorize.getResponse().getRedirectedUrl();
    assertThat(location).startsWith("http://127.0.0.1:8080/callback?code=");
    String code = UriComponentsBuilder.fromUriString(location).build()
        .getQueryParams().getFirst("code");

    // 3. The public client exchanges the code plus the verifier, with no secret
    MvcResult tokenResult = mvc.perform(post("/oauth2/token")
            .param("grant_type", "authorization_code")
            .param("client_id", "recipe-web")
            .param("code", code)
            .param("redirect_uri", "http://127.0.0.1:8080/callback")
            .param("code_verifier", verifier))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id_token").exists())
        .andExpect(jsonPath("$.refresh_token").doesNotExist())
        .andReturn();

    String token = JsonPath.read(tokenResult.getResponse().getContentAsString(), "$.access_token");
    Jwt jwt = jwtDecoder.decode(token);
    assertThat(jwt.getSubject()).isEqualTo("lokesh");
    assertThat(jwt.getClaimAsStringList("roles")).containsExactly("CHEF");
    assertThat(jwt.getClaimAsStringList("scope")).containsExactlyInAnyOrder("openid", "recipes.read");
  }

  @Test
  void wrongCodeVerifierIsRejected() throws Exception {
    MvcResult authorize = mvc.perform(get("/oauth2/authorize")
            .with(user("lokesh").roles("CHEF"))
            .queryParam("response_type", "code")
            .queryParam("client_id", "recipe-web")
            .queryParam("redirect_uri", "http://127.0.0.1:8080/callback")
            .queryParam("scope", "recipes.read")
            .queryParam("code_challenge", s256("the-right-verifier-the-right-verifier-123"))
            .queryParam("code_challenge_method", "S256"))
        .andReturn();
    String code = UriComponentsBuilder.fromUriString(authorize.getResponse().getRedirectedUrl())
        .build().getQueryParams().getFirst("code");

    mvc.perform(post("/oauth2/token")
            .param("grant_type", "authorization_code")
            .param("client_id", "recipe-web")
            .param("code", code)
            .param("redirect_uri", "http://127.0.0.1:8080/callback")
            .param("code_verifier", "a-different-verifier-a-different-verifier"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("invalid_grant"));
  }

  @Test
  void authorizeWithoutPkceIsRejected() throws Exception {
    MvcResult result = mvc.perform(get("/oauth2/authorize")
            .with(user("lokesh").roles("CHEF"))
            .queryParam("response_type", "code")
            .queryParam("client_id", "recipe-web")
            .queryParam("redirect_uri", "http://127.0.0.1:8080/callback")
            .queryParam("scope", "recipes.read"))
        .andExpect(status().is3xxRedirection())
        .andReturn();

    assertThat(result.getResponse().getRedirectedUrl())
        .startsWith("http://127.0.0.1:8080/callback?error=invalid_request");
  }

  @Test
  void unauthenticatedBrowserIsSentToLogin() throws Exception {
    mvc.perform(get("/oauth2/authorize")
            .accept("text/html")
            .queryParam("response_type", "code")
            .queryParam("client_id", "recipe-web")
            .queryParam("redirect_uri", "http://127.0.0.1:8080/callback")
            .queryParam("scope", "recipes.read")
            .queryParam("code_challenge", s256("some-verifier-some-verifier-some-verifier"))
            .queryParam("code_challenge_method", "S256"))
        .andExpect(status().is3xxRedirection())
        .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers
            .redirectedUrl("/login"));
  }

  static String s256(String verifier) throws Exception {
    byte[] digest = MessageDigest.getInstance("SHA-256")
        .digest(verifier.getBytes(StandardCharsets.US_ASCII));
    return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
  }
}
