package com.howtodoinjava.quartz;

import static com.howtodoinjava.quartz.QuartzConfig.FINE_JOB;
import static com.howtodoinjava.quartz.QuartzConfig.FINE_NOTICE_JOB;
import static com.howtodoinjava.quartz.QuartzConfig.REMINDER_JOB;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.quartz.JobDataMap;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerMetaData;
import org.quartz.simpl.RAMJobStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.howtodoinjava.quartz.jobs.LoanReminderScheduler;
import com.howtodoinjava.quartz.library.LibraryRepository;
import com.howtodoinjava.quartz.library.SentMessage;
import com.howtodoinjava.quartz.listeners.JobAuditListener;
import com.howtodoinjava.quartz.listeners.JobRun;

@SpringBootTest
class LibraryJobsTest {

  @Autowired Scheduler scheduler;
  @Autowired JobAuditListener jobAudit;
  @Autowired LibraryRepository repository;
  @Autowired LoanReminderScheduler loanReminderScheduler;

  @BeforeEach
  void reset() {
    jobAudit.clear();
    repository.deleteSentMessagesAndFines();
  }

  @Test
  void schedulerUsesRamJobStoreWithFiveThreads() throws Exception {
    SchedulerMetaData meta = scheduler.getMetaData();
    assertThat(meta.getVersion()).isEqualTo("2.5.2");
    assertThat(meta.getJobStoreClass()).isEqualTo(RAMJobStore.class);
    assertThat(meta.isJobStoreSupportsPersistence()).isFalse();
    assertThat(meta.getThreadPoolSize()).isEqualTo(5);
    assertThat(scheduler.getJobKeys(org.quartz.impl.matchers.GroupMatcher.jobGroupEquals("library")))
        .containsExactlyInAnyOrder(REMINDER_JOB, FINE_JOB, FINE_NOTICE_JOB);
  }

  @Test
  void reminderJobEmailsMembersWithBooksDueTomorrow() throws Exception {
    scheduler.triggerJob(REMINDER_JOB);

    await().atMost(Duration.ofSeconds(5)).until(() -> jobAudit.runsOf("library.dueDateReminderJob").size() == 1);
    assertThat(jobAudit.runsOf("library.dueDateReminderJob").getFirst().result()).isEqualTo(1);
    assertThat(repository.sentMessages()).containsExactly(
        new SentMessage(1, "REMINDER", "anna", "'Dune' is due on " + LocalDate.now().plusDays(1)));
  }

  @Test
  void triggerDataOverridesJobData() throws Exception {
    scheduler.triggerJob(REMINDER_JOB, new JobDataMap(Map.of("daysBefore", 3)));

    await().atMost(Duration.ofSeconds(5)).until(() -> jobAudit.runsOf("library.dueDateReminderJob").size() == 1);
    assertThat(repository.sentMessages()).containsExactly(
        new SentMessage(2, "REMINDER", "ben", "'Emma' is due on " + LocalDate.now().plusDays(3)));
    // the stored JobDetail keeps its own value
    assertThat(scheduler.getJobDetail(REMINDER_JOB).getJobDataMap().getInt("daysBefore")).isEqualTo(1);
  }

  @Test
  void fineJobChainsToFineNoticeJob() throws Exception {
    scheduler.triggerJob(FINE_JOB);

    await().atMost(Duration.ofSeconds(5)).until(() -> jobAudit.runsOf("library.fineNoticeJob").size() == 1);
    JobRun fineRun = jobAudit.runsOf("library.overdueFineJob").getFirst();
    JobRun noticeRun = jobAudit.runsOf("library.fineNoticeJob").getFirst();
    assertThat(fineRun.result()).isEqualTo(2);
    assertThat(noticeRun.result()).isEqualTo(2);
    assertThat(noticeRun.fireTime()).isAfterOrEqualTo(fineRun.fireTime());
    assertThat(repository.sentMessages()).containsExactly(
        new SentMessage(3, "FINE", "carla", "Fine of 1.00 for 'Ulysses' (4 days late)"),
        new SentMessage(4, "FINE", "dev", "Fine of 2.50 for 'Hamlet' (10 days late)"));
  }

  @Test
  void fineJobKeepsItsRunCountBetweenRuns() throws Exception {
    int before = scheduler.getJobDetail(FINE_JOB).getJobDataMap().getInt("runCount");

    scheduler.triggerJob(FINE_JOB);
    await().atMost(Duration.ofSeconds(5)).until(() -> jobAudit.runsOf("library.overdueFineJob").size() == 1);
    scheduler.triggerJob(FINE_JOB);
    await().atMost(Duration.ofSeconds(5)).until(() -> jobAudit.runsOf("library.overdueFineJob").size() == 2);

    JobDataMap data = scheduler.getJobDetail(FINE_JOB).getJobDataMap();
    assertThat(data.getInt("runCount")).isEqualTo(before + 2);
    assertThat(data.getString("lastTotal")).isEqualTo("3.50");
  }

  @Test
  void jobWithoutTriggerMustBeDurable() {
    org.quartz.JobDetail notDurable = org.quartz.JobBuilder.newJob(com.howtodoinjava.quartz.jobs.FineNoticeJob.class)
        .withIdentity("notDurable", "test")
        .build();
    org.assertj.core.api.Assertions.assertThatThrownBy(() -> scheduler.addJob(notDurable, true))
        .isInstanceOf(org.quartz.SchedulerException.class)
        .hasMessage("Jobs added with no trigger must be durable.");
  }

  @Test
  void oneTimeReminderRunsOnceAndIsRemoved() throws Exception {
    loanReminderScheduler.scheduleReminder(2, Instant.now().plusSeconds(1));
    JobKey key = JobKey.jobKey("loan-2", "reminders");
    assertThat(scheduler.checkExists(key)).isTrue();

    await().atMost(Duration.ofSeconds(5)).until(() -> jobAudit.runsOf("reminders.loan-2").size() == 1);
    assertThat(repository.sentMessages()).extracting(SentMessage::recipient).isEqualTo(List.of("ben"));
    await().atMost(Duration.ofSeconds(2)).until(() -> !scheduler.checkExists(key));
  }
}
