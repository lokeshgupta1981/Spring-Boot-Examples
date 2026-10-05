package com.howtodoinjava.datasource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot application with one auto-configured HikariCP DataSource (recipes)
 * and one additional DataSource (audit) defined in {@link AuditDataSourceConfig}.
 */
@SpringBootApplication
public class DataSourceConfigApplication {

  public static void main(String[] args) {
    SpringApplication.run(DataSourceConfigApplication.class, args);
  }
}
