package com.howtodoinjava.annotations;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {
    private final ObjectProvider<GreetingService> service;

    public GreetingController(ObjectProvider<GreetingService> service) {
        this.service = service;
    }

    @GetMapping("/greeting")
    String greeting() {
        GreetingService available = service.getIfAvailable();
        return available == null ? "Greeting is disabled" : available.greeting();
    }
}
