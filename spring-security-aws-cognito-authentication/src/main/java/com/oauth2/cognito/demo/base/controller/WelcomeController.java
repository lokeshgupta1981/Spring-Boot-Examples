package com.oauth2.cognito.demo.base.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class WelcomeController {

    @GetMapping("/")
    public String main(Model model) {
        model.addAttribute("message", "Welcome To The Index Page!");
        return "index";
    }

    @GetMapping("/greetMe")
    public String home(Authentication authentication, Model model) {
        boolean isAuthenticated = authentication.isAuthenticated();

        String response = "";

        if (isAuthenticated) {
            DefaultOidcUser defaultOidcUser = (DefaultOidcUser) authentication.getPrincipal();
            Map<String, Object> userAttributes = defaultOidcUser.getAttributes();

            response += "Welcome " + userAttributes.get("cognito:username") + " ! God Bless You With Amazing Future Ahead :)";
        } else {
            response += "Not Authenticated!";
        }
        model.addAttribute("response", response);
        return "greeting";
    }
}
