package com.howtodoinjava.demo;

import com.howtodoinjava.demo.repositories.MovieDetailRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(excludeFilters =
@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = MovieDetailRepository.class))
public class SpringDataDynamodbExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringDataDynamodbExampleApplication.class, args);
    }

}
