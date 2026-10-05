package com.howtodoinjava.quartz;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

import org.junit.jupiter.api.Test;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.PersistJobDataAfterExecution;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.howtodoinjava.quartz.listeners.JobAuditListener;

@SpringBootTest
class ConcurrencyTest {

  static final ConcurrentHashMap<String, AtomicInteger> running = new ConcurrentHashMap<>();
  static final ConcurrentHashMap<String, AtomicInteger> maxRunning = new ConcurrentHashMap<>();

  /** Takes 500 ms and records how many copies of itself run at the same time. */
  public static class SlowReportJob extends QuartzJobBean {
    @Override
    protected void executeInternal(JobExecutionContext context) {
      String name = context.getJobDetail().getKey().getName();
      int now = running.computeIfAbsent(name, n -> new AtomicInteger()).incrementAndGet();
      maxRunning.computeIfAbsent(name, n -> new AtomicInteger()).accumulateAndGet(now, Math::max);
      LockSupport.parkNanos(Duration.ofMillis(500).toNanos());
      running.get(name).decrementAndGet();
    }
  }

  @DisallowConcurrentExecution
  public static class SingleSlowReportJob extends SlowReportJob {
  }

  public static class CountingJob extends QuartzJobBean {
    @Override
    protected void executeInternal(JobExecutionContext context) {
      JobDataMap data = context.getJobDetail().getJobDataMap();
      data.put("count", data.getInt("count") + 1);
      context.setResult(data.getInt("count"));
    }
  }

  @PersistJobDataAfterExecution
  public static class PersistentCountingJob extends CountingJob {
  }

  @Autowired Scheduler scheduler;
  @Autowired JobAuditListener jobAudit;

  @Test
  void disallowConcurrentExecutionRunsOneCopyAtATime() throws Exception {
    JobKey parallel = JobKey.jobKey("parallelReport", "test");
    JobKey single = JobKey.jobKey("singleReport", "test");
    scheduler.addJob(JobBuilder.newJob(SlowReportJob.class).withIdentity(parallel).storeDurably().build(), true);
    scheduler.addJob(JobBuilder.newJob(SingleSlowReportJob.class).withIdentity(single).storeDurably().build(), true);

    for (int i = 0; i < 3; i++) {
      scheduler.triggerJob(parallel);
      scheduler.triggerJob(single);
    }

    await().atMost(Duration.ofSeconds(5)).until(() ->
        jobAudit.runsOf("test.parallelReport").size() == 3 && jobAudit.runsOf("test.singleReport").size() == 3);
    System.out.println("Max copies at the same time: parallelReport=" + maxRunning.get("parallelReport")
        + ", singleReport=" + maxRunning.get("singleReport"));
    assertThat(maxRunning.get("parallelReport").get()).isEqualTo(3);
    assertThat(maxRunning.get("singleReport").get()).isEqualTo(1);
  }

  @Test
  void persistJobDataAfterExecutionKeepsTheCounter() throws Exception {
    JobKey plain = JobKey.jobKey("plainCounter", "test");
    JobKey persistent = JobKey.jobKey("persistentCounter", "test");
    scheduler.addJob(JobBuilder.newJob(CountingJob.class).withIdentity(plain)
        .usingJobData("count", 0).storeDurably().build(), true);
    scheduler.addJob(JobBuilder.newJob(PersistentCountingJob.class).withIdentity(persistent)
        .usingJobData("count", 0).storeDurably().build(), true);

    for (int i = 1; i <= 3; i++) {
      int expected = i;
      scheduler.triggerJob(plain);
      scheduler.triggerJob(persistent);
      await().atMost(Duration.ofSeconds(5)).until(() ->
          jobAudit.runsOf("test.plainCounter").size() == expected
              && jobAudit.runsOf("test.persistentCounter").size() == expected);
    }

    System.out.println("plainCounter results: " + jobAudit.runsOf("test.plainCounter").stream().map(r -> r.result()).toList());
    System.out.println("persistentCounter results: " + jobAudit.runsOf("test.persistentCounter").stream().map(r -> r.result()).toList());
    assertThat(scheduler.getJobDetail(plain).getJobDataMap().getInt("count")).isZero();
    assertThat(scheduler.getJobDetail(persistent).getJobDataMap().getInt("count")).isEqualTo(3);
  }
}
