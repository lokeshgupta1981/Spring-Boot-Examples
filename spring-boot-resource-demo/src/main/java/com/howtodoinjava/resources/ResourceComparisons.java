package com.howtodoinjava.resources;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
public class ResourceComparisons {
    private final ResourceLoader loader;

    public ResourceComparisons(ResourceLoader loader) {
        this.loader = loader;
    }

    public String withClassPathResource() throws IOException {
        return read(new ClassPathResource("mail/order-confirmation.txt"));
    }

    public String withResourceLoader() throws IOException {
        return read(loader.getResource("classpath:mail/order-confirmation.txt"));
    }

    public boolean hasPhysicalFile() {
        try {
            new ClassPathResource("mail/order-confirmation.txt").getFile();
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    private String read(Resource resource) throws IOException {
        try (var input = resource.getInputStream()) {
            return new String(input.readAllBytes(), StandardCharsets.UTF_8).trim();
        }
    }
}
