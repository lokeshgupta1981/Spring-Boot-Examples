package com.howtodoinjava.quartz;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.quartz.JobBuilder;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.testcontainers.postgresql.PostgreSQLContainer;

import com.howtodoinjava.quartz.listeners.JobAuditListener;

/** Two application instances share one PostgreSQL database and form a Quartz cluster. */
class ClusterTest {

  public static class InventoryJob extends QuartzJobBean {
    @Override
    protected void executeInternal(JobExecutionContext context) {
    }
  }

  static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18.6");

  @BeforeAll
  static void startDatabase() {
    postgres.start();
  }

  @AfterAll
  static void stopDatabase() {
    postgres.stop();
  }

  ConfigurableApplicationContext startNode(String initializeSchema) {
    return new SpringApplicationBuilder(LibraryApplication.class)
        .run(
            "--spring.main.web-application-type=none",
            "--spring.datasource.url=" + postgres.getJdbcUrl(),
            "--spring.datasource.username=" + postgres.getUsername(),
            "--spring.datasource.password=" + postgres.getPassword(),
            "--spring.sql.init.mode=never",
            "--spring.quartz.job-store-type=jdbc",
            "--spring.quartz.jdbc.initialize-schema=" + initializeSchema,
            "--spring.quartz.properties.org.quartz.jobStore.driverDelegateClass=org.quartz.impl.jdbcjobstore.PostgreSQLDelegate",
            "--spring.quartz.properties.org.quartz.jobStore.isClustered=true",
            "--spring.quartz.properties.org.quartz.scheduler.instanceId=AUTO");
  }

  @Test
  void twoNodesRunEachTriggerOnce() throws Exception {
    try (ConfigurableApplicationContext node1 = startNode("always");
         ConfigurableApplicationContext node2 = startNode("never")) {

      Scheduler scheduler1 = node1.getBean(Scheduler.class);
      Scheduler scheduler2 = node2.getBean(Scheduler.class);
      assertThat(scheduler1.getMetaData().isJobStoreClustered()).isTrue();
      assertThat(scheduler1.getSchedulerInstanceId()).isNotEqualTo(scheduler2.getSchedulerInstanceId());

      JdbcClient jdbc = node1.getBean(JdbcClient.class);
      await().atMost(Duration.ofSeconds(20)).until(() ->
          jdbc.sql("select count(*) from qrtz_scheduler_state").query(Integer.class).single() == 2);
      List<String> instances = jdbc.sql("select instance_name from qrtz_scheduler_state order by 1").query(String.class).list();
      System.out.println("QRTZ_SCHEDULER_STATE: " + instances);

      JobKey inventory = JobKey.jobKey("inventoryJob", "cluster");
      scheduler1.addJob(JobBuilder.newJob(InventoryJob.class).withIdentity(inventory).storeDurably().build(), true);
      JobAuditListener audit1 = node1.getBean(JobAuditListener.class);
      JobAuditListener audit2 = node2.getBean(JobAuditListener.class);
      for (int i = 0; i < 6; i++) {
        (i % 2 == 0 ? scheduler1 : scheduler2).triggerJob(inventory);
      }
      await().atMost(Duration.ofSeconds(30)).until(() ->
          audit1.runsOf("cluster.inventoryJob").size() + audit2.runsOf("cluster.inventoryJob").size() == 6);
      await().during(Duration.ofSeconds(2)).atMost(Duration.ofSeconds(3)).until(() ->
          audit1.runsOf("cluster.inventoryJob").size() + audit2.runsOf("cluster.inventoryJob").size() == 6);
      System.out.println("inventoryJob runs: node1=" + audit1.runsOf("cluster.inventoryJob").size()
          + ", node2=" + audit2.runsOf("cluster.inventoryJob").size());
    }
  }
}
