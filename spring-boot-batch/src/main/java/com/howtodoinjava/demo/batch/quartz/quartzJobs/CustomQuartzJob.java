package com.howtodoinjava.demo.batch.quartz.quartzJobs;

import org.quartz.JobExecutionContext;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Component
public class CustomQuartzJob extends QuartzJobBean {

  String jobName;

  JobOperator jobOperator;
  ApplicationContext applicationContext;

  public CustomQuartzJob(JobOperator jobOperator, ApplicationContext applicationContext) {
    this.jobOperator = jobOperator;
    this.applicationContext = applicationContext;
  }

  public String getJobName() {
    return jobName;
  }

  public void setJobName(String jobName) {
    this.jobName = jobName;
  }

  @Override
  protected void executeInternal(JobExecutionContext context) {
    try {
      Job job = applicationContext.getBean(jobName, Job.class);

      JobParameters params = new JobParametersBuilder()
          .addString("JobID", String.valueOf(System.currentTimeMillis()))
          .toJobParameters();

      jobOperator.start(job, params);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
