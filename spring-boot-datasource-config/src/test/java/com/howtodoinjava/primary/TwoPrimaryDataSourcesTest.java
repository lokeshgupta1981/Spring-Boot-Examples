package com.howtodoinjava.primary;

import static org.assertj.core.api.Assertions.assertThat;

import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/** The classic setup: two DataSource beans, one marked @Primary. Auto-configuration backs off. */
@SpringBootTest(classes = TwoPrimaryDataSourcesTest.TwoDataSourcesConfig.class, properties = {
    "spring.config.name=none",
    "spring.datasource.url=jdbc:h2:mem:ignored",
    "app.datasource.recipes.jdbc-url=jdbc:h2:mem:recipes2",
    "app.datasource.recipes.username=sa",
    "app.datasource.recipes.maximum-pool-size=5",
    "app.datasource.audit.jdbc-url=jdbc:h2:mem:audit2",
    "app.datasource.audit.username=sa"
})
class TwoPrimaryDataSourcesTest {

  @Configuration(proxyBeanMethods = false)
  @EnableAutoConfiguration
  static class TwoDataSourcesConfig {

    @Bean
    @Primary
    @ConfigurationProperties("app.datasource.recipes")
    DataSource recipesDataSource() {
      return DataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties("app.datasource.audit")
    DataSource auditDataSource() {
      return DataSourceBuilder.create().build();
    }
  }

  @Autowired DataSource dataSource;
  @Autowired @Qualifier("auditDataSource") DataSource audit;
  @Autowired java.util.Map<String, DataSource> all;

  @Test
  void primaryIsInjectedByDefaultAndQualifierPicksTheOther() {
    assertThat(all.keySet()).containsExactlyInAnyOrder("recipesDataSource", "auditDataSource");
    assertThat(((HikariDataSource) dataSource).getJdbcUrl()).isEqualTo("jdbc:h2:mem:recipes2");
    assertThat(((HikariDataSource) dataSource).getMaximumPoolSize()).isEqualTo(5);
    assertThat(((HikariDataSource) audit).getJdbcUrl()).isEqualTo("jdbc:h2:mem:audit2");
  }
}
