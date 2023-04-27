package com.howtodoinjava.app.web;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.oidc.authentication.OidcAuthorizationCodeAuthenticationProvider;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Iterator;
import java.util.List;

@RestController
public class HomeController {


    @GetMapping("/public")
    public String publicResource(){
        return "public Resource ";
    }
    @GetMapping("/")
    public String protectedResource(HttpSession httpSession ){

        Iterator<String> iterator = httpSession.getAttributeNames().asIterator();
        while (iterator.hasNext()){
            System.out.println(iterator.next());
        }
        return "protected Resource " + httpSession.getAttribute("SPRING_SECURITY_CONTEXT");
    }
}
