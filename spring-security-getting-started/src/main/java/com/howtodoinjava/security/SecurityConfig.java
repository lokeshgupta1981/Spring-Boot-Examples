package com.howtodoinjava.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Replaces Spring Boot's default security: public URLs, role-based URLs, form login, HTTP Basic,
 * and two in-memory users.
 */
@Configuration
public class SecurityConfig {

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/public/**", "/error").permitAll()  // no login needed
            .requestMatchers("/admin/**").hasRole("ADMIN")         // only users with role ADMIN
            .anyRequest().authenticated())                         // any logged-in user
        .formLogin(Customizer.withDefaults())                      // login page for browsers
        .httpBasic(Customizer.withDefaults());                     // Authorization header for curl
    return http.build();
  }

  @Bean
  UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
    UserDetails lokesh = User.withUsername("lokesh")
        .password(passwordEncoder.encode("password"))
        .roles("USER")
        .build();
    UserDetails admin = User.withUsername("admin")
        .password(passwordEncoder.encode("admin123"))
        .roles("USER", "ADMIN")
        .build();
    return new InMemoryUserDetailsManager(lokesh, admin);
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();   // bcrypt by default
  }
}
