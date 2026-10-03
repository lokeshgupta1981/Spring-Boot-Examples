package com.howtodoinjava.quartz;

import static com.howtodoinjava.quartz.QuartzConfig.FINE_JOB;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.awaitility.Awaitility.await;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.quartz.JobBuilder;
import org.quartz.JobKey;
import org.quartz.JobPersistenceException;
import org.quartz.Scheduler;
import org.quartz.SchedulerMetaData;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.scheduling.quartz.LocalDataSourceJobStore;

import com.howtodoinjava.quartz.jobs.LoanReminderJob;
import com.howtodoinjava.quartz.jobs.LoanReminderScheduler;
import com.howtodoinjava.quartz.library.Loan;
import com.howtodoinjava.quartz.library.LibraryRepository;
import com.howtodoinjava.quartz.library.SentMessage;
import com.howtodoinjava.quartz.listeners.JobAuditListener;
import com.howtodoinjava.quartz.listeners.TriggerAuditListener;

/** Starts the application three times on the same H2 file database, like three restarts. */
class JdbcJobStoreTest {

  final String url = "jdbc:h2:file:./target/h2-" + UUID.randomUUID() + "/library";

  ConfigurableApplicationContext start(String initializeSchema) {
    return new SpringApplicationBuilder(LibraryApplication.class)
        .profiles("jdbc")
        .run(
            "--spring.quartz.properties.org.quartz.jobStore.misfireThreshold=1000",
            "--spring.main.web-application-type=none",
            "--spring.datasource.url=" + url,
            "--spring.quartz.jdbc.initialize-schema=" + initializeSchema);
  }

  @Test
  void jobsAndJobDataSurviveARestart() throws Exception {
    JobKey loan3 = JobKey.jobKey("loan-3", "reminders");

    // 1. First start: create the Quartz tables, schedule a reminder for tomorrow, run the fine job once
    try (ConfigurableApplicationContext app = start("always")) {
      Scheduler scheduler = app.getBean(Scheduler.class);
      SchedulerMetaData meta = scheduler.getMetaData();
      assertThat(meta.getJobStoreClass()).isEqualTo(LocalDataSourceJobStore.class);
      assertThat(meta.isJobStoreSupportsPersistence()).isTrue();

      app.getBean(LoanReminderScheduler.class).scheduleReminder(3, Instant.now().plus(Duration.ofDays(1)));
      scheduler.triggerJob(FINE_JOB);
      JobAuditListener audit = app.getBean(JobAuditListener.class);
      await().atMost(Duration.ofSeconds(10)).until(() -> audit.runsOf("library.fineNoticeJob").size() == 1);
      await().atMost(Duration.ofSeconds(5))
          .until(() -> scheduler.getJobDetail(FINE_JOB).getJobDataMap().getInt("runCount") == 1);

      JdbcClient jdbc = app.getBean(JdbcClient.class);
      System.out.println("QRTZ_JOB_DETAILS: " + jdbc.sql("select job_group || '.' || job_name from qrtz_job_details order by 1").query(String.class).list());
      System.out.println("QRTZ_TRIGGERS: " + jdbc.sql("select trigger_group || '.' || trigger_name || ' ' || trigger_type || ' ' || trigger_state from qrtz_triggers order by 1").query(String.class).list());
      assertThat(jdbc.sql("select count(*) from qrtz_job_details").query(Integer.class).single()).isEqualTo(4);
      assertThat(jdbc.sql("select count(*) from qrtz_triggers").query(Integer.class).single()).isEqualTo(3);
    }

    // 2. Restart with initialize-schema=never: the reminder and the run counter are still there
    try (ConfigurableApplicationContext app = start("never")) {
      Scheduler scheduler = app.getBean(Scheduler.class);
      assertThat(scheduler.checkExists(loan3)).isTrue();
      assertThat(scheduler.getJobDetail(FINE_JOB).getJobDataMap().getInt("runCount")).isEqualTo(1);
    }

    // 3. Restart with initialize-schema=always: the H2 script has no DROP TABLE, its CREATE TABLE
    //    statements fail and Spring Boot continues (spring.quartz.jdbc.continue-on-error=true)
    try (ConfigurableApplicationContext app = start("always")) {
      Scheduler scheduler = app.getBean(Scheduler.class);
      assertThat(scheduler.checkExists(loan3)).isTrue();
      assertThat(scheduler.getJobDetail(FINE_JOB).getJobDataMap().getInt("runCount")).isEqualTo(1);
    }
  }

  @Test
  void reminderMissedWhileTheAppWasDownRunsAtStartup() throws Exception {
    Instant sendAt = Instant.now().plusSeconds(5);
    try (ConfigurableApplicationContext app = start("always")) {
      app.getBean(LoanReminderScheduler.class).scheduleReminder(1, sendAt);   // fire now on misfire
    }
    // the application is down when the reminder is due
    await().atMost(Duration.ofSeconds(15)).until(() -> Instant.now().isAfter(sendAt.plusSeconds(2)));

    try (ConfigurableApplicationContext app = start("never")) {
      JobAuditListener audit = app.getBean(JobAuditListener.class);
      await().atMost(Duration.ofSeconds(10)).until(() -> audit.runsOf("reminders.loan-1").size() == 1);
      assertThat(app.getBean(TriggerAuditListener.class).misfired()).contains("reminders.loan-1");
      assertThat(app.getBean(LibraryRepository.class).sentMessages())
          .extracting(SentMessage::recipient).contains("anna");
    }
  }

  @Test
  void embeddedModeCreatesNoTablesInAFileDatabase() {
    assertThatThrownBy(() -> start("embedded").close())
        .satisfies(e -> {
          Throwable root = org.assertj.core.util.Throwables.getRootCause(e);
          System.out.println("Root cause: " + root.getMessage().lines().findFirst().orElse(""));
          assertThat(root.getMessage()).contains("QRTZ_");
        });
  }

  @Test
  void jobDataMustBeSerializable() {
    try (ConfigurableApplicationContext app = start("always")) {
      Scheduler scheduler = app.getBean(Scheduler.class);
      Loan loan = new Loan(1, "anna", "Dune", null, null);   // a record that is not Serializable
      assertThatThrownBy(() -> scheduler.addJob(JobBuilder.newJob(LoanReminderJob.class)
              .withIdentity("loan-1", "reminders")
              .usingJobData(new org.quartz.JobDataMap(java.util.Map.of("loan", loan)))
              .storeDurably()
              .build(), false))
          .isInstanceOf(JobPersistenceException.class)
          .satisfies(e -> System.out.println("Error: " + e.getMessage()));
    }
  }
}
