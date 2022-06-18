package com.howtodoinjava.demo.config;

import lombok.extern.java.Log;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.sql.SQLException;

@Log
@Configuration
public class JpaConfig {
  @Profile("local")
  @Bean
  public DataSource inMemoryDataSource() {
    log.info("Setting up H2 DataSource");

    DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
    dataSourceBuilder.driverClassName("org.h2.Driver");
    dataSourceBuilder.url("jdbc:h2:file:C:/temp/test");
    dataSourceBuilder.username("sa");
    dataSourceBuilder.password("");
    return dataSourceBuilder.build();
  }

  @Profile("dev")
  @Bean
  public DataSource mysqlDataSource() {
    log.info("Setting up MySql DataSource");

    DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
    dataSourceBuilder.url("jdbc:mysql://localhost/testdb");
    dataSourceBuilder.username("dbuser");
    dataSourceBuilder.password("dbpass");
    return dataSourceBuilder.build();
  }

  @Profile("prod")
  @Bean
  public DataSource oracleDataSource() throws SQLException {
    log.info("Setting up Oracle DataSource");

    DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
    dataSourceBuilder.url("jdbc:oracle:thin:@localhost:1521:xe");
    dataSourceBuilder.username("system");
    dataSourceBuilder.password("manager");
    return dataSourceBuilder.build();
  }
}
