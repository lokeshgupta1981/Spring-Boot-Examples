package com.howtodoinjava.quartz;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.postgresql.PostgreSQLContainer;

import com.howtodoinjava.quartz.jobs.LoanReminderScheduler;

/** On PostgreSQL, the Quartz script starts with DROP TABLE: initialize-schema=always deletes every job on restart. */
class PostgresRestartTest {

  static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18.6");

  @BeforeAll
  static void startDatabase() {
    postgres.start();
  }

  @AfterAll
  static void stopDatabase() {
    postgres.stop();
  }

  ConfigurableApplicationContext start(String initializeSchema) {
    return start(initializeSchema, "org.quartz.impl.jdbcjobstore.PostgreSQLDelegate");
  }

  ConfigurableApplicationContext start(String initializeSchema, String delegate) {
    return new SpringApplicationBuilder(LibraryApplication.class)
        .run(
            "--spring.quartz.properties.org.quartz.jobStore.driverDelegateClass=" + delegate,
            "--spring.main.web-application-type=none",
            "--spring.datasource.url=" + postgres.getJdbcUrl(),
            "--spring.datasource.username=" + postgres.getUsername(),
            "--spring.datasource.password=" + postgres.getPassword(),
            "--spring.sql.init.mode=never",
            "--spring.quartz.job-store-type=jdbc",
            "--spring.quartz.jdbc.initialize-schema=" + initializeSchema);
  }

  @Test
  void standardDelegateCannotReadJobDataFromPostgres() {
    assertThatThrownBy(() -> start("always", "org.quartz.impl.jdbcjobstore.StdJDBCDelegate").close())
        .hasRootCauseInstanceOf(org.postgresql.util.PSQLException.class)
        .rootCause()
        .hasMessageStartingWith("Bad value for type long")
        .satisfies(e -> System.out.println("Root cause: " + e.getMessage().substring(0, 40) + "..."));
  }

  @Test
  void initializeSchemaAlwaysDeletesJobsOnPostgres() throws Exception {
    JobKey loan3 = JobKey.jobKey("loan-3", "reminders");

    try (ConfigurableApplicationContext app = start("always")) {
      app.getBean(LoanReminderScheduler.class).scheduleReminder(3, Instant.now().plus(Duration.ofDays(1)));
      assertThat(app.getBean(Scheduler.class).checkExists(loan3)).isTrue();
    }
    try (ConfigurableApplicationContext app = start("never")) {
      assertThat(app.getBean(Scheduler.class).checkExists(loan3)).isTrue();
    }
    try (ConfigurableApplicationContext app = start("always")) {
      assertThat(app.getBean(Scheduler.class).checkExists(loan3)).isFalse();
    }
  }
}
