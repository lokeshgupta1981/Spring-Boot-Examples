package com.howtodoinjava.datasource;

import com.zaxxer.hikari.HikariDataSource;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.boot.jdbc.init.DataSourceScriptDatabaseInitializer;
import org.springframework.boot.sql.init.DatabaseInitializationMode;
import org.springframework.boot.sql.init.DatabaseInitializationSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Adds a second DataSource next to the auto-configured one. defaultCandidate = false keeps
 * the auto-configured DataSource in place, so plain DataSource injection still gets "recipes".
 */
@Configuration(proxyBeanMethods = false)
public class AuditDataSourceConfig {

  @Bean(defaultCandidate = false)
  @Qualifier("audit")
  @ConfigurationProperties("app.datasource.audit")
  DataSourceProperties auditDataSourceProperties() {
    return new DataSourceProperties();
  }

  @Bean(defaultCandidate = false)
  @Qualifier("audit")
  @ConfigurationProperties("app.datasource.audit.configuration")
  HikariDataSource auditDataSource(@Qualifier("audit") DataSourceProperties properties) {
    return properties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
  }

  // spring.sql.init.* runs only on the auto-configured DataSource; the audit database needs its own initializer
  @Bean
  DataSourceScriptDatabaseInitializer auditDatabaseInitializer(@Qualifier("audit") DataSource audit) {
    DatabaseInitializationSettings settings = new DatabaseInitializationSettings();
    settings.setSchemaLocations(List.of("classpath:audit-schema.sql"));
    settings.setMode(DatabaseInitializationMode.ALWAYS);
    return new DataSourceScriptDatabaseInitializer(audit, settings);
  }
}
