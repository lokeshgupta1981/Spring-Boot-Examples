package com.howtodoinjava.sample;

import com.howtodoinjava.referenceid.ReferenceIdFormatter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SampleApplication {
    public static void main(String[] args) {
        SpringApplication.run(SampleApplication.class, args);
    }

    @Bean
    CommandLineRunner showReferenceId(ReferenceIdFormatter formatter) {
        return args -> System.out.println(formatter.format(42));
    }
}
