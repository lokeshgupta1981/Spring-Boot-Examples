package com.howtodoinjava.k8s;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class VisitRepository {

  private final JdbcClient jdbc;

  public VisitRepository(JdbcClient jdbc) {
    this.jdbc = jdbc;
  }

  /** Records one visit from the given pod and returns the total number of visits. */
  public long recordVisit(String pod) {
    jdbc.sql("insert into visit (pod) values (?)").param(pod).update();
    return jdbc.sql("select count(*) from visit").query(Long.class).single();
  }
}
