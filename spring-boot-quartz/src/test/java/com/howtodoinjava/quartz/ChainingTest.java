package com.howtodoinjava.quartz;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.quartz.JobBuilder;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.matchers.KeyMatcher;
import org.quartz.listeners.JobChainingJobListener;
import org.quartz.listeners.JobListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.howtodoinjava.quartz.listeners.JobAuditListener;

@SpringBootTest
class ChainingTest {

  public static class FailingFineJob extends QuartzJobBean {
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
      throw new JobExecutionException("database is down");
    }
  }

  public static class NoticeJob extends QuartzJobBean {
    @Override
    protected void executeInternal(JobExecutionContext context) {
    }
  }

  /** Starts the next job only when the previous one finished without an exception. */
  static class OnSuccessChainListener extends JobListenerSupport {
    private final JobKey first;
    private final JobKey next;

    OnSuccessChainListener(JobKey first, JobKey next) {
      this.first = first;
      this.next = next;
    }

    @Override
    public String getName() {
      return "onSuccess-" + first;
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException error) {
      if (error == null && context.getJobDetail().getKey().equals(first)) {
        try {
          context.getScheduler().triggerJob(next);
        } catch (SchedulerException e) {
          getLog().error("Could not start {}", next, e);
        }
      }
    }
  }

  @Autowired Scheduler scheduler;
  @Autowired JobAuditListener jobAudit;

  @Test
  void jobChainingJobListenerRunsTheNextJobEvenAfterAFailure() throws Exception {
    JobKey failing = addJob("failingA", FailingFineJob.class);
    JobKey notice = addJob("noticeA", NoticeJob.class);
    JobChainingJobListener chain = new JobChainingJobListener("chainA");
    chain.addJobChainLink(failing, notice);
    scheduler.getListenerManager().addJobListener(chain, KeyMatcher.keyEquals(failing));

    scheduler.triggerJob(failing);

    await().atMost(Duration.ofSeconds(5)).until(() -> jobAudit.runsOf("chain.noticeA").size() == 1);
    assertThat(jobAudit.runsOf("chain.failingA").getFirst().error()).isEqualTo("database is down");
  }

  @Test
  void customListenerStopsTheChainAfterAFailure() throws Exception {
    JobKey failing = addJob("failingB", FailingFineJob.class);
    JobKey notice = addJob("noticeB", NoticeJob.class);
    scheduler.getListenerManager().addJobListener(new OnSuccessChainListener(failing, notice),
        KeyMatcher.keyEquals(failing));

    scheduler.triggerJob(failing);

    await().atMost(Duration.ofSeconds(5)).until(() -> jobAudit.runsOf("chain.failingB").size() == 1);
    await().during(Duration.ofSeconds(1)).atMost(Duration.ofSeconds(2))
        .until(() -> jobAudit.runsOf("chain.noticeB").isEmpty());
  }

  private JobKey addJob(String name, Class<? extends QuartzJobBean> type) throws SchedulerException {
    JobKey key = JobKey.jobKey(name, "chain");
    scheduler.addJob(JobBuilder.newJob(type).withIdentity(key).storeDurably().build(), true);
    return key;
  }
}
