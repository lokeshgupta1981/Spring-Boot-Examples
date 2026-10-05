package com.howtodoinjava.quartz;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.jupiter.api.Test;
import org.quartz.JobBuilder;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.listeners.SchedulerListenerSupport;

import com.howtodoinjava.quartz.jobs.DueDateReminderJob;

/** Without Spring's SpringBeanJobFactory, Quartz cannot create a job that needs a Spring bean. */
class PlainQuartzTest {

  @Test
  void quartzDefaultJobFactoryCannotInjectBeans() throws Exception {
    Properties props = new Properties();
    props.setProperty("org.quartz.scheduler.instanceName", "plainQuartz");
    props.setProperty("org.quartz.threadPool.threadCount", "1");
    props.setProperty("org.quartz.jobStore.class", "org.quartz.simpl.RAMJobStore");
    Scheduler scheduler = new StdSchedulerFactory(props).getScheduler();

    List<String> errors = new CopyOnWriteArrayList<>();
    scheduler.getListenerManager().addSchedulerListener(new SchedulerListenerSupport() {
      @Override
      public void schedulerError(String msg, SchedulerException cause) {
        errors.add(msg + " | " + cause.getMessage() + " | " + cause.getCause());
      }
    });
    scheduler.scheduleJob(JobBuilder.newJob(DueDateReminderJob.class).withIdentity("reminder").build(),
        TriggerBuilder.newTrigger().startNow().build());
    scheduler.start();
    try {
      await().atMost(Duration.ofSeconds(5)).until(() -> !errors.isEmpty());
      errors.forEach(System.out::println);
      assertThat(errors.getFirst()).contains("An error occurred instantiating job to be executed");
    } finally {
      scheduler.shutdown();
    }
  }
}
