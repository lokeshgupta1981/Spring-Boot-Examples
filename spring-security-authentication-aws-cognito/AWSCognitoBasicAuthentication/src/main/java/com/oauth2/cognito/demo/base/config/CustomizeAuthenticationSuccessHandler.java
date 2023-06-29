package com.oauth2.cognito.demo.base.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class CustomizeAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        for (GrantedAuthority auth : authentication.getAuthorities()) {

            DefaultOidcUser defaultOidcUser = (DefaultOidcUser) authentication.getPrincipal();

            Map<String, Object> userAttributes = defaultOidcUser.getAttributes();

            System.out.println(userAttributes);

            if ("ROLE_ADMIN".equals(auth.getAuthority())) {
                System.out.println(userAttributes.get("cognito:username") + " Is Admin!");
                response.sendRedirect("/admin/greetMe");
            } else if ("ROLE_USER".equals(auth.getAuthority())) {
                System.out.println(userAttributes.get("cognito:username") + " Is User!");
                response.sendRedirect("/user/greetMe");
            }
        }
    }
}
