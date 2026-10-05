package com.howtodoinjava.quartz;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.ScheduleBuilder;
import org.quartz.Scheduler;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.howtodoinjava.quartz.listeners.JobAuditListener;
import com.howtodoinjava.quartz.listeners.JobRun;
import com.howtodoinjava.quartz.listeners.TriggerAuditListener;

/**
 * Eight triggers fire every 5 seconds. We put the scheduler in standby for two fire times,
 * start it again halfway between two fire times, and count what runs in the next 1.5 seconds.
 */
@SpringBootTest(properties = "spring.quartz.properties.org.quartz.jobStore.misfireThreshold=1000")
class MisfireTest {

  public static class NightlyReportJob extends QuartzJobBean {
    @Override
    protected void executeInternal(JobExecutionContext context) {
    }
  }

  @Autowired Scheduler scheduler;
  @Autowired JobAuditListener jobAudit;
  @Autowired TriggerAuditListener triggerAudit;

  @Test
  void eachMisfireInstructionHandlesMissedRunsDifferently() throws Exception {
    JobKey job = JobKey.jobKey("nightlyReport", "misfire");
    scheduler.addJob(JobBuilder.newJob(NightlyReportJob.class).withIdentity(job).storeDurably().build(), true);

    // first fire time on a 5-second boundary, at least 1 second from now
    Date first = new CronExpression("0/5 * * * * ?").getNextValidTimeAfter(new Date(System.currentTimeMillis() + 1000));

    Map<String, ScheduleBuilder<? extends Trigger>> schedules = new LinkedHashMap<>();
    schedules.put("simple-smart-policy", SimpleScheduleBuilder.repeatSecondlyForever(5));
    schedules.put("simple-fire-now", SimpleScheduleBuilder.repeatSecondlyForever(5).withMisfireHandlingInstructionFireNow());
    schedules.put("simple-next-with-remaining-count",
        SimpleScheduleBuilder.repeatSecondlyForever(5).withMisfireHandlingInstructionNextWithRemainingCount());
    schedules.put("simple-ignore-misfires",
        SimpleScheduleBuilder.repeatSecondlyForever(5).withMisfireHandlingInstructionIgnoreMisfires());
    schedules.put("cron-smart-policy", CronScheduleBuilder.cronSchedule("0/5 * * * * ?"));
    schedules.put("cron-fire-and-proceed",
        CronScheduleBuilder.cronSchedule("0/5 * * * * ?").withMisfireHandlingInstructionFireAndProceed());
    schedules.put("cron-do-nothing",
        CronScheduleBuilder.cronSchedule("0/5 * * * * ?").withMisfireHandlingInstructionDoNothing());
    schedules.put("cron-ignore-misfires",
        CronScheduleBuilder.cronSchedule("0/5 * * * * ?").withMisfireHandlingInstructionIgnoreMisfires());

    // 1. Pause the scheduler, then schedule all triggers
    scheduler.standby();
    jobAudit.clear();
    triggerAudit.clear();
    for (var entry : schedules.entrySet()) {
      scheduler.scheduleJob(TriggerBuilder.newTrigger()
          .forJob(job)
          .withIdentity(entry.getKey(), "misfire")
          .startAt(first)
          .withSchedule(entry.getValue())
          .build());
    }

    // 2. Stay paused through two fire times (first and first + 5 s)
    Date resumeAt = new Date(first.getTime() + 7_500);
    await().atMost(Duration.ofSeconds(15)).until(() -> System.currentTimeMillis() >= resumeAt.getTime());

    // 3. Resume and watch for 1.5 seconds; the next regular fire time is 2.5 seconds away
    Date resumedAt = new Date();
    scheduler.start();
    await().atMost(Duration.ofSeconds(3)).until(() -> System.currentTimeMillis() >= resumedAt.getTime() + 1_500);

    SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss.SSS");
    System.out.println("Missed fire times: " + time.format(first) + ", " + time.format(new Date(first.getTime() + 5000))
        + "; resumed at " + time.format(resumedAt));
    Map<String, Integer> runsAfterResume = new LinkedHashMap<>();
    for (String name : schedules.keySet()) {
      List<JobRun> runs = jobAudit.runs().stream().filter(r -> r.trigger().equals("misfire." + name)).toList();
      runsAfterResume.put(name, runs.size());
      Trigger trigger = scheduler.getTrigger(org.quartz.TriggerKey.triggerKey(name, "misfire"));
      System.out.printf("%-34s runs=%d scheduled=%s next=%s misfireListener=%s%n", name, runs.size(),
          runs.stream().map(r -> time.format(r.scheduledFireTime())).toList(),
          time.format(trigger.getNextFireTime()), triggerAudit.misfired().contains("misfire." + name));
    }
    scheduler.deleteJob(job);

    assertThat(runsAfterResume).containsExactly(
        Map.entry("simple-smart-policy", 0),
        Map.entry("simple-fire-now", 1),
        Map.entry("simple-next-with-remaining-count", 0),
        Map.entry("simple-ignore-misfires", 2),
        Map.entry("cron-smart-policy", 1),
        Map.entry("cron-fire-and-proceed", 1),
        Map.entry("cron-do-nothing", 0),
        Map.entry("cron-ignore-misfires", 2));
    // IGNORE_MISFIRE_POLICY never calls triggerMisfired()
    assertThat(triggerAudit.misfired()).doesNotContain("misfire.simple-ignore-misfires", "misfire.cron-ignore-misfires")
        .contains("misfire.simple-fire-now", "misfire.cron-do-nothing");
  }
}
