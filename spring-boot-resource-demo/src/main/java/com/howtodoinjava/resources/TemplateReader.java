package com.howtodoinjava.resources;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class TemplateReader {
    private final Resource template;

    public TemplateReader(@Value("classpath:mail/order-confirmation.txt") Resource template) {
        this.template = template;
    }

    public String messageFor(String name) throws IOException {
        try (var input = template.getInputStream()) {
            String text = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            return text.replace("{name}", name).trim();
        }
    }
}
