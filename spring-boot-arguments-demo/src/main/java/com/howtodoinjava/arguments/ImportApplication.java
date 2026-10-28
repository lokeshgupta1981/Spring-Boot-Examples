package com.howtodoinjava.arguments;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ImportApplication implements CommandLineRunner {
    private final ApplicationArguments arguments;
    private final int batchSize;

    public ImportApplication(ApplicationArguments arguments,
            @Value("${import.batch-size}") int batchSize) {
        this.arguments = arguments;
        this.batchSize = batchSize;
    }

    public static void main(String[] args) {
        SpringApplication.run(ImportApplication.class, args);
    }

    @Override
    public void run(String... rawArgs) {
        List<String> paths = arguments.getNonOptionArgs();
        if (paths.size() != 1) {
            throw new IllegalArgumentException("Provide one source path");
        }

        List<String> regions = arguments.getOptionValues("region");
        if (regions == null || regions.size() != 1 || regions.get(0).isBlank()) {
            throw new IllegalArgumentException("Provide one --region=value option");
        }

        System.out.println("source=" + paths.get(0));
        System.out.println("region=" + regions.get(0));
        System.out.println("dryRun=" + arguments.containsOption("dry-run"));
        System.out.println("batchSize=" + batchSize);
    }
}
