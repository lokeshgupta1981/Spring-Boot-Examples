package com.howtodoinjava.resources;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ResourceApplication implements CommandLineRunner {
    private final TemplateReader templates;
    private final ResourceComparisons comparisons;

    public ResourceApplication(TemplateReader templates, ResourceComparisons comparisons) {
        this.templates = templates;
        this.comparisons = comparisons;
    }

    public static void main(String[] args) {
        SpringApplication.run(ResourceApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println(templates.messageFor("Lokesh"));
        System.out.println("ClassPathResource: " + comparisons.withClassPathResource());
        System.out.println("ResourceLoader: " + comparisons.withResourceLoader());
        System.out.println("Physical file available: " + comparisons.hasPhysicalFile());
    }
}
