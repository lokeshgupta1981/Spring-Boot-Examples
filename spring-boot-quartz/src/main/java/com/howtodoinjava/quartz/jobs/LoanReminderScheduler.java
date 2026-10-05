package com.howtodoinjava.quartz.jobs;

import java.time.Instant;
import java.util.Date;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.stereotype.Service;

@Service
public class LoanReminderScheduler {

  private final Scheduler scheduler;

  public LoanReminderScheduler(Scheduler scheduler) {
    this.scheduler = scheduler;
  }

  public Date scheduleReminder(long loanId, Instant sendAt) throws SchedulerException {
    JobDetail job = JobBuilder.newJob(LoanReminderJob.class)
        .withIdentity("loan-" + loanId, "reminders")
        .usingJobData("loanId", loanId)
        .build();

    Trigger trigger = TriggerBuilder.newTrigger()
        .withIdentity("loan-" + loanId, "reminders")
        .startAt(Date.from(sendAt))
        .withSchedule(SimpleScheduleBuilder.simpleSchedule()
            .withMisfireHandlingInstructionFireNow())
        .build();

    return scheduler.scheduleJob(job, trigger);
  }
}
