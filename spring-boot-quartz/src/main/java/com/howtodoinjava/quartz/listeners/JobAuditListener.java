package com.howtodoinjava.quartz.listeners;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** Logs every job execution and keeps the last runs in memory. */
@Component
public class JobAuditListener implements JobListener {

  private static final Logger log = LoggerFactory.getLogger(JobAuditListener.class);

  private final List<JobRun> runs = new CopyOnWriteArrayList<>();

  @Override
  public String getName() {
    return "jobAudit";
  }

  @Override
  public void jobToBeExecuted(JobExecutionContext context) {
    log.info("Starting {}", context.getJobDetail().getKey());
  }

  @Override
  public void jobExecutionVetoed(JobExecutionContext context) {
    log.warn("Vetoed {}", context.getJobDetail().getKey());
  }

  @Override
  public void jobWasExecuted(JobExecutionContext context, JobExecutionException error) {
    JobRun run = new JobRun(context.getJobDetail().getKey().toString(), context.getTrigger().getKey().toString(),
        context.getScheduledFireTime(), context.getFireTime(), context.getJobRunTime(), context.getResult(),
        error == null ? null : error.getMessage());
    runs.add(run);
    if (error == null) {
      log.info("Finished {} in {} ms, result={}", run.job(), run.runTimeMs(), run.result());
    } else {
      log.error("Failed {} in {} ms: {}", run.job(), run.runTimeMs(), run.error());
    }
  }

  public List<JobRun> runs() {
    return List.copyOf(runs);
  }

  public List<JobRun> runsOf(String jobKey) {
    return runs.stream().filter(run -> run.job().equals(jobKey)).toList();
  }

  public void clear() {
    runs.clear();
  }
}
