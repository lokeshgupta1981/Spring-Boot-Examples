package com.howtodoinjava.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity(debug = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests()
                .anyRequest().authenticated()
                .and()
                .oauth2Login();
       return  httpSecurity.build();
    }



    @Bean
    public ClientRegistrationRepository clientRepository() {
        ClientRegistration keycloak = keycloakClientRegistration();
        return new InMemoryClientRegistrationRepository(keycloak);
    }

    private ClientRegistration keycloakClientRegistration() {
        return ClientRegistration.withRegistrationId("employee-api")
                .clientId("employee-api")
                .clientSecret("8SBeGY3JM9xa6F3zdEcu6QHuI08bSVaH")
                .redirectUri("http://localhost:9090/login/oauth2/code/employee-api")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .issuerUri("http://localhost:8080/auth/realms/howtodoinjava")
                .authorizationUri("http://localhost:8080/auth/realms/howtodoinjava/protocol/openid-connect/auth")
                .tokenUri("http://localhost:8080/auth/realms/howtodoinjava/protocol/openid-connect/token")
                .userInfoUri("http://localhost:8080/auth/realms/howtodoinjava/protocol/openid-connect/userinfo")
                .userNameAttributeName("sub")
                .build();
    }
}
