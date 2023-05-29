package com.oauth2.cognito.demo.base.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(request -> request.requestMatchers("/").permitAll().anyRequest().authenticated())
                .oauth2Login(oauth -> oauth.redirectionEndpoint(endPoint -> endPoint.baseUri("/login/oauth2/code/cognito")));
        return http.build();
    }
}

