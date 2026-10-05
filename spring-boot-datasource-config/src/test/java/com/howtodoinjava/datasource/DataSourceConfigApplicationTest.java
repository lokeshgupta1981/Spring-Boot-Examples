package com.howtodoinjava.datasource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.zaxxer.hikari.HikariDataSource;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.simple.JdbcClient;

@SpringBootTest
class DataSourceConfigApplicationTest {

  @Autowired DataSource dataSource;
  @Autowired @Qualifier("audit") DataSource auditDataSource;
  @Autowired RecipeService recipeService;

  @Test
  void mainDataSourceIsHikariFromSpringDatasourceProperties() {
    HikariDataSource hikari = (HikariDataSource) dataSource;
    assertThat(hikari.getJdbcUrl()).isEqualTo("jdbc:h2:mem:recipes");
    assertThat(hikari.getUsername()).isEqualTo("sa");
    assertThat(hikari.getDriverClassName()).isEqualTo("org.h2.Driver");
    assertThat(hikari.getPoolName()).isEqualTo("recipes-pool");
    assertThat(hikari.getMaximumPoolSize()).isEqualTo(10);
    assertThat(hikari.getMinimumIdle()).isEqualTo(2);
    assertThat(hikari.getConnectionTimeout()).isEqualTo(30000);
    assertThat(hikari.getIdleTimeout()).isEqualTo(600000);
    assertThat(hikari.getMaxLifetime()).isEqualTo(1800000);
  }

  @Test
  void auditDataSourceIsASecondPool() {
    HikariDataSource hikari = (HikariDataSource) auditDataSource;
    assertThat(hikari.getJdbcUrl()).isEqualTo("jdbc:h2:mem:audit");
    assertThat(hikari.getPoolName()).isEqualTo("audit-pool");
    assertThat(hikari.getMaximumPoolSize()).isEqualTo(3);
    assertThat(auditDataSource).isNotSameAs(dataSource);
  }

  @Test
  void sqlInitRunsOnMainDataSourceOnly() {
    assertThat(recipeService.recipeNames()).isEqualTo(List.of("Pancakes", "Omelette"));
    assertThat(recipeService.auditEvents()).isGreaterThanOrEqualTo(1);
    // data.sql was not run on the audit database
    assertThatThrownBy(() -> JdbcClient.create(auditDataSource)
        .sql("select count(*) from recipe").query(Long.class).single())
        .isInstanceOf(BadSqlGrammarException.class)
        .rootCause().hasMessageContaining("Table \"RECIPE\" not found");
  }
}
