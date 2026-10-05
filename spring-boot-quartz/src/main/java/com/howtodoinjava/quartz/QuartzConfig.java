package com.howtodoinjava.quartz;

import static org.quartz.CronScheduleBuilder.cronSchedule;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.listeners.JobChainingJobListener;
import org.springframework.boot.quartz.autoconfigure.SchedulerFactoryBeanCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.howtodoinjava.quartz.jobs.DueDateReminderJob;
import com.howtodoinjava.quartz.jobs.FineNoticeJob;
import com.howtodoinjava.quartz.jobs.OverdueFineJob;
import com.howtodoinjava.quartz.listeners.JobAuditListener;
import com.howtodoinjava.quartz.listeners.TriggerAuditListener;

@Configuration
public class QuartzConfig {

  public static final JobKey REMINDER_JOB = JobKey.jobKey("dueDateReminderJob", "library");
  public static final JobKey FINE_JOB = JobKey.jobKey("overdueFineJob", "library");
  public static final JobKey FINE_NOTICE_JOB = JobKey.jobKey("fineNoticeJob", "library");

  // 1. Reminder emails every day at 08:00
  @Bean
  JobDetail reminderJob() {
    return JobBuilder.newJob(DueDateReminderJob.class)
        .withIdentity(REMINDER_JOB)
        .usingJobData("daysBefore", 1)
        .storeDurably()
        .build();
  }

  @Bean
  Trigger reminderTrigger(JobDetail reminderJob) {
    return TriggerBuilder.newTrigger()
        .forJob(reminderJob)
        .withIdentity("reminderTrigger", "library")
        .withSchedule(cronSchedule("0 0 8 * * ?"))
        .build();
  }

  // 2. Fine calculation every night at 02:00, then the fine notices
  @Bean
  JobDetail fineJob() {
    return JobBuilder.newJob(OverdueFineJob.class)
        .withIdentity(FINE_JOB)
        .usingJobData("runCount", 0)
        .storeDurably()
        .build();
  }

  @Bean
  Trigger fineTrigger(JobDetail fineJob) {
    return TriggerBuilder.newTrigger()
        .forJob(fineJob)
        .withIdentity("fineTrigger", "library")
        .withSchedule(cronSchedule("0 0 2 * * ?")
            .withMisfireHandlingInstructionFireAndProceed())
        .build();
  }

  @Bean
  JobDetail fineNoticeJob() {
    return JobBuilder.newJob(FineNoticeJob.class)
        .withIdentity(FINE_NOTICE_JOB)
        .storeDurably()          // no trigger of its own
        .build();
  }

  // 3. Listeners: audit every job and trigger, run fineNoticeJob after fineJob
  @Bean
  JobChainingJobListener fineChain() {
    JobChainingJobListener chain = new JobChainingJobListener("fineChain");
    chain.addJobChainLink(FINE_JOB, FINE_NOTICE_JOB);
    return chain;
  }

  @Bean
  SchedulerFactoryBeanCustomizer listeners(JobAuditListener jobAudit, TriggerAuditListener triggerAudit,
                                           JobChainingJobListener fineChain) {
    return factory -> {
      factory.setGlobalJobListeners(jobAudit, fineChain);
      factory.setGlobalTriggerListeners(triggerAudit);
    };
  }
}
