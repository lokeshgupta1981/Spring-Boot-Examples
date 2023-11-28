package com.howtodoinjava.demo.batch.quartz.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

  DataSource dataSource;

  public DataSourceConfig(DataSource dataSource){
    this.dataSource = dataSource;
  }

  @Bean
  public DataSourceTransactionManager transactionManager() {
    return new DataSourceTransactionManager(dataSource);
  }

  @Bean
  public DataSourceInitializer databasePopulator() {
    ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
    populator.addScript(new ClassPathResource("org/springframework/batch/core/schema-h2.sql"));
    populator.setContinueOnError(false);
    populator.setIgnoreFailedDrops(false);
    DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
    dataSourceInitializer.setDataSource(dataSource);
    dataSourceInitializer.setDatabasePopulator(populator);
    return dataSourceInitializer;
  }
}
