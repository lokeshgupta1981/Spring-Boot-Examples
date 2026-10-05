package com.howtodoinjava.datasource;

import java.util.List;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;

/** Reads recipes from the main DataSource and writes audit events to the audit DataSource. */
@Service
public class RecipeService {

  private final JdbcClient recipes;
  private final JdbcClient audit;

  public RecipeService(DataSource dataSource, @Qualifier("audit") DataSource auditDataSource) {
    this.recipes = JdbcClient.create(dataSource);
    this.audit = JdbcClient.create(auditDataSource);
  }

  public List<String> recipeNames() {
    audit.sql("insert into audit_event (message) values (?)").param("recipes listed").update();
    return recipes.sql("select name from recipe order by id").query(String.class).list();
  }

  public long auditEvents() {
    return audit.sql("select count(*) from audit_event").query(Long.class).single();
  }
}
