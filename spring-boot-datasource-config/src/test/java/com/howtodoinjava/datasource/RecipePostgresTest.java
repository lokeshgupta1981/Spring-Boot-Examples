package com.howtodoinjava.datasource;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.JdbcTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

/** Runs the same schema.sql and data.sql against PostgreSQL in Docker. Needs a running Docker daemon. */
@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers(disabledWithoutDocker = true)
class RecipePostgresTest {

  @Container
  @ServiceConnection
  static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17-alpine");

  @Autowired DataSource dataSource;
  @Autowired JdbcClient jdbcClient;

  @Test
  void runsAgainstPostgres() throws Exception {
    try (var connection = dataSource.getConnection()) {
      String url = connection.getMetaData().getURL();
      System.out.println("Testcontainers url = " + url + ", product = "
          + connection.getMetaData().getDatabaseProductName());
      assertThat(url).startsWith("jdbc:postgresql://");
    }
    List<String> names = jdbcClient.sql("select name from recipe order by id").query(String.class).list();
    assertThat(names).isEqualTo(List.of("Pancakes", "Omelette"));
  }
}
