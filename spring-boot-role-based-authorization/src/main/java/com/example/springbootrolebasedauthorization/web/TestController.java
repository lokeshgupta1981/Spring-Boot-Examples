package com.example.springbootrolebasedauthorization.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@EnableMethodSecurity
@RestController
public class TestController {

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/test")
    public Authentication getAuthenticatedUser() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @GetMapping("/noRoleNeeded")
    public String publicResource() {
        return "This resource only requires authentication";
    }

    @GetMapping("/roleNeeded")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String privateResource() {
        return "This resource requires the ADMIN role";
    }
}

