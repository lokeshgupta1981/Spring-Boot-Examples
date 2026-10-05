package com.howtodoinjava.security7;

import static org.springframework.security.config.Customizer.withDefaults;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationManagerFactories;
import org.springframework.security.authorization.DefaultAuthorizationManagerFactory;
import org.springframework.security.config.annotation.authorization.EnableMultiFactorAuthentication;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.FactorGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.ott.OneTimeTokenGenerationSuccessHandler;
import org.springframework.security.web.authentication.ott.RedirectOneTimeTokenGenerationSuccessHandler;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMultiFactorAuthentication(authorities = {})
public class SecurityConfig {

  private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) {
    // Password AND one-time token, only for the URLs that use "mfa"
    DefaultAuthorizationManagerFactory<Object> mfa = AuthorizationManagerFactories.multiFactor()
        .requireFactors(FactorGrantedAuthority.PASSWORD_AUTHORITY, FactorGrantedAuthority.OTT_AUTHORITY)
        .build();

    PathPatternRequestMatcher deleteExpense = PathPatternRequestMatcher.withDefaults()
        .matcher(HttpMethod.DELETE, "/expenses/{name}");

    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/public/**").permitAll()
            .requestMatchers(deleteExpense).hasRole("ADMIN")
            .requestMatchers("/reports/**").access(mfa.hasRole("ADMIN"))
            .requestMatchers("/audit/**").hasAllRoles("ADMIN", "AUDITOR")
            .anyRequest().authenticated())
        .formLogin(withDefaults())
        .httpBasic(withDefaults())
        .oneTimeTokenLogin(withDefaults())
        .csrf(csrf -> csrf.spa());

    return http.build();
  }

  @Bean
  OneTimeTokenGenerationSuccessHandler ottSuccessHandler() {
    RedirectOneTimeTokenGenerationSuccessHandler redirect = new RedirectOneTimeTokenGenerationSuccessHandler("/ott/sent");
    return (request, response, token) -> {
      // A real app emails this link to the user; the demo writes it to the log
      log.info("Login link for {}: http://localhost:8080/login/ott?token={}", token.getUsername(), token.getTokenValue());
      redirect.handle(request, response, token);
    };
  }

  @Bean
  UserDetailsService users(PasswordEncoder encoder) {
    return new InMemoryUserDetailsManager(
        User.withUsername("lokesh").password(encoder.encode("secret")).roles("USER").build(),
        User.withUsername("anna").password(encoder.encode("secret")).roles("ADMIN").build());
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }
}
