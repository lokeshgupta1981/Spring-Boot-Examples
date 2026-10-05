package com.howtodoinjava.datasource;

import static org.assertj.core.api.Assertions.assertThat;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

class DataSourceBuilderTest {

  @Test
  void builderCreatesHikariByDefault() throws Exception {
    DataSource ds = DataSourceBuilder.create()
        .url("jdbc:h2:mem:builder")
        .username("sa")
        .password("")
        .build();
    assertThat(ds).isInstanceOf(HikariDataSource.class);
    HikariDataSource hikari = (HikariDataSource) ds;
    assertThat(hikari.getJdbcUrl()).isEqualTo("jdbc:h2:mem:builder");
    assertThat(hikari.getDriverClassName()).isEqualTo("org.h2.Driver");   // deduced from the URL
    try (Connection c = ds.getConnection()) {
      assertThat(c.getMetaData().getDatabaseProductName()).isEqualTo("H2");
    }
    hikari.close();
  }

  @Test
  void builderCanCreateOtherTypes() {
    DataSource ds = DataSourceBuilder.create()
        .type(SimpleDriverDataSource.class)
        .url("jdbc:h2:mem:simple")
        .username("sa")
        .build();
    assertThat(ds).isInstanceOf(SimpleDriverDataSource.class);
  }
}
