package com.howtodoinjava.authserver;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.List;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

/**
 * Signing keys. The current key signs new tokens. The previous key is published as a
 * public key only, so tokens signed before the rotation still validate until they expire.
 * A real server loads both keys from a keystore or a vault instead of generating them.
 */
@Configuration(proxyBeanMethods = false)
public class KeyConfig {

  static final String CURRENT_KEY_ID = "recipe-key-2";
  static final String PREVIOUS_KEY_ID = "recipe-key-1";

  @Bean
  JWKSource<SecurityContext> jwkSource() {
    RSAKey current = generateRsaKey(CURRENT_KEY_ID);                  // public + private
    RSAKey previous = generateRsaKey(PREVIOUS_KEY_ID).toPublicJWK();  // public only
    return new ImmutableJWKSet<>(new JWKSet(List.of(current, previous)));
  }

  @Bean
  JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
    NimbusJwtEncoder encoder = new NimbusJwtEncoder(jwkSource);
    // Both keys match RS256, so pick the one that has a private key
    encoder.setJwkSelector(keys -> keys.stream()
        .filter(JWK::isPrivate)
        .findFirst()
        .orElseThrow());
    return encoder;
  }

  private static RSAKey generateRsaKey(String keyId) {
    try {
      KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
      generator.initialize(2048);
      KeyPair keyPair = generator.generateKeyPair();
      return new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
          .privateKey((RSAPrivateKey) keyPair.getPrivate())
          .keyID(keyId)
          .build();
    } catch (NoSuchAlgorithmException ex) {
      throw new IllegalStateException("RSA is not available", ex);
    }
  }
}
