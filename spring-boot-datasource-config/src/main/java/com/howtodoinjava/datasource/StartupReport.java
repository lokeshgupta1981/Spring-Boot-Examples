package com.howtodoinjava.datasource;

import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/** Prints which pool and URL each DataSource uses, and the data read through both. */
@Component
public class StartupReport implements CommandLineRunner {

  private final DataSource dataSource;
  private final DataSource auditDataSource;
  private final RecipeService recipeService;

  public StartupReport(DataSource dataSource, @Qualifier("audit") DataSource auditDataSource,
      RecipeService recipeService) {
    this.dataSource = dataSource;
    this.auditDataSource = auditDataSource;
    this.recipeService = recipeService;
  }

  @Override
  public void run(String... args) {
    print("main ", dataSource);
    print("audit", auditDataSource);
    System.out.println("recipes      = " + recipeService.recipeNames());
    System.out.println("audit events = " + recipeService.auditEvents());
  }

  private static void print(String label, DataSource ds) {
    HikariDataSource hikari = (HikariDataSource) ds;
    System.out.printf("%s: %s %s pool=%s max=%d%n", label, ds.getClass().getSimpleName(),
        hikari.getJdbcUrl(), hikari.getPoolName(), hikari.getMaximumPoolSize());
  }
}
