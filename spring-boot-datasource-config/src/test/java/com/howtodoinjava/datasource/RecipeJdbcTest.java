package com.howtodoinjava.datasource;

import static org.assertj.core.api.Assertions.assertThat;

import com.zaxxer.hikari.HikariDataSource;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.JdbcTest;
import org.springframework.jdbc.core.simple.JdbcClient;

/** @JdbcTest replaces the configured DataSource with an embedded H2 database. */
@JdbcTest
class RecipeJdbcTest {

  @Autowired DataSource dataSource;
  @Autowired JdbcClient jdbcClient;

  @Test
  void runsAgainstEmbeddedDatabaseWithSchemaAndData() {
    System.out.println("@JdbcTest DataSource = " + dataSource.getClass().getSimpleName());
    assertThat(dataSource).isNotInstanceOf(HikariDataSource.class);
    List<String> names = jdbcClient.sql("select name from recipe order by id").query(String.class).list();
    assertThat(names).isEqualTo(List.of("Pancakes", "Omelette"));
  }
}
