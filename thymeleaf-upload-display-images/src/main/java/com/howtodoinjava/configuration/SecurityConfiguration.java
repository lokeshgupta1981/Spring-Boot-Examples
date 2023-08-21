package com.howtodoinjava.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    http.authorizeHttpRequests(request -> request
        .requestMatchers("/profiles/*").permitAll()
        .requestMatchers("/images/*").permitAll()
        .anyRequest().authenticated());

    return http.build();
  }
}
