package com.howtodoinjava.demo;

import com.howtodoinjava.demo.repositories.MovieDetailRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
/**
 * Below code excludes spring jpa and h2 as to assume it as SQL tables
 */
@EnableJpaRepositories(excludeFilters =
@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
    value = MovieDetailRepository.class))
public class App {

  public static void main(String[] args) {
    SpringApplication.run(App.class, args);
  }

}
