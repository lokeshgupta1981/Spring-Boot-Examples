package com.howtodoinjava.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class BatchJobLauncherScheduler {

  private static final Logger LOGGER = LoggerFactory.getLogger(BatchJobLauncherScheduler.class);

  private final Job job;
  private final JobLauncher jobLauncher;

  @Autowired
  public BatchJobLauncherScheduler(@Qualifier("excelFileToDatabaseJob") Job job, JobLauncher jobLauncher) {
    this.job = job;
    this.jobLauncher = jobLauncher;
  }

  @Scheduled(cron = "${excel.to.database.job.cron}")
  void launchExcelFileToDatabaseJob() throws JobParametersInvalidException,
      JobExecutionAlreadyRunningException,
      JobRestartException, JobInstanceAlreadyCompleteException {

    LOGGER.info("Starting excelFileToDatabase job");
    jobLauncher.run(job, newExecution());
    LOGGER.info("Stopping excelFileToDatabase job");
  }

  private JobParameters newExecution() {

    JobParameters jobParameters = new JobParametersBuilder()
        .addJobParameter("currentTimestamp", new JobParameter(LocalDateTime.now(), LocalDateTime.class))
        .toJobParameters();

    return jobParameters;
  }
}