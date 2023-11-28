package com.howtodoinjava.demo.batch.quartz.job;

import lombok.Getter;
import lombok.Setter;
import org.quartz.JobExecutionContext;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.quartz.QuartzJobBean;

@Setter
@Getter
public class CustomQuartzJobBean extends QuartzJobBean {

  private String jobName;
  private JobLauncher jobLauncher;
  private JobLocator jobLocator;

  @Override
  protected void executeInternal(JobExecutionContext context) {
    try {
      Job job = jobLocator.getJob(jobName);

      JobParameters params = new JobParametersBuilder()
          .addString("JobID", String.valueOf(System.currentTimeMillis()))
          .toJobParameters();

      jobLauncher.run(job, params);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
