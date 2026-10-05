package com.howtodoinjava.datasource;

import static org.assertj.core.api.Assertions.assertThat;

import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

class AutoConfigurationRulesTest {

  private final ApplicationContextRunner runner = new ApplicationContextRunner()
      .withConfiguration(AutoConfigurations.of(DataSourceAutoConfiguration.class));

  @Test
  void noUrlWithH2OnClasspathGivesEmbeddedDatabase() {
    runner.run(context -> {
      HikariDataSource ds = (HikariDataSource) context.getBean(DataSource.class);
      System.out.println("embedded url = " + ds.getJdbcUrl() + ", user = " + ds.getUsername());
      assertThat(ds.getJdbcUrl()).startsWith("jdbc:h2:mem:").hasSizeGreaterThan(30);
      assertThat(ds.getUsername()).isEqualTo("sa");
    });
  }

  @Test
  void fixedEmbeddedNameWhenUniqueNameIsOff() {
    runner.withPropertyValues("spring.datasource.generate-unique-name=false").run(context -> {
      HikariDataSource ds = (HikariDataSource) context.getBean(DataSource.class);
      assertThat(ds.getJdbcUrl()).startsWith("jdbc:h2:mem:testdb");
    });
  }

  @Test
  void noUrlAndNoEmbeddedDatabaseFails() {
    runner.withClassLoader(new FilteredClassLoader("org.h2", "org.hsqldb", "org.apache.derby"))
        .run(context -> {
          assertThat(context).hasFailed();
          Throwable root = context.getStartupFailure();
          while (root.getCause() != null) {
            root = root.getCause();
          }
          System.out.println("root cause = " + root.getClass().getName() + ": " + root.getMessage());
          assertThat(root).hasMessageContaining("Failed to determine a suitable driver class");
        });
  }

  @Test
  void spring_datasource_type_selects_the_pool() {
    runner.withPropertyValues("spring.datasource.url=jdbc:h2:mem:typed",
            "spring.datasource.type=org.springframework.jdbc.datasource.SimpleDriverDataSource")
        .run(context -> assertThat(context.getBean(DataSource.class).getClass().getSimpleName())
            .isEqualTo("SimpleDriverDataSource"));
  }

  @Configuration(proxyBeanMethods = false)
  static class OwnDataSource {
    @Bean
    @ConfigurationProperties("app.datasource")
    DataSource dataSource() {
      return DataSourceBuilder.create().build();
    }
  }

  @Test
  void ownDataSourceBeanMakesAutoConfigurationBackOff() {
    runner.withUserConfiguration(OwnDataSource.class)
        .withPropertyValues("spring.datasource.url=jdbc:h2:mem:ignored",
            "app.datasource.jdbc-url=jdbc:h2:mem:mine")
        .run(context -> {
          assertThat(context).hasSingleBean(DataSource.class);
          assertThat(((HikariDataSource) context.getBean(DataSource.class)).getJdbcUrl())
              .isEqualTo("jdbc:h2:mem:mine");
        });
  }

  @Test
  void hikariHasNoUrlProperty() {
    runner.withUserConfiguration(OwnDataSource.class)
        .withPropertyValues("app.datasource.url=jdbc:h2:mem:mine")
        .run(context -> {
          HikariDataSource ds = (HikariDataSource) context.getBean(DataSource.class);
          assertThat(ds.getJdbcUrl()).isNull();
          try {
            ds.getConnection();
          } catch (Exception e) {
            System.out.println("url binding error = " + e.getClass().getName() + ": " + e.getMessage());
            assertThat(e).hasMessageContaining("jdbcUrl is required");
          }
        });
  }

  @Configuration(proxyBeanMethods = false)
  static class TwoDataSourcesNoPrimary {
    @Bean
    DataSource recipesDataSource() {
      return DataSourceBuilder.create().url("jdbc:h2:mem:r").build();
    }

    @Bean
    DataSource auditDataSource() {
      return DataSourceBuilder.create().url("jdbc:h2:mem:a").build();
    }

    @Bean
    String consumer(DataSource dataSource) {
      return dataSource.toString();
    }
  }

  @Test
  void twoDataSourcesWithoutPrimaryFailOnPlainInjection() {
    runner.withUserConfiguration(TwoDataSourcesNoPrimary.class).run(context -> {
      assertThat(context).hasFailed();
      Throwable root = context.getStartupFailure();
      while (root.getCause() != null) {
        root = root.getCause();
      }
      System.out.println("two beans = " + root.getClass().getSimpleName() + ": " + root.getMessage());
      assertThat(root).isInstanceOf(org.springframework.beans.factory.NoUniqueBeanDefinitionException.class);
    });
  }
}
