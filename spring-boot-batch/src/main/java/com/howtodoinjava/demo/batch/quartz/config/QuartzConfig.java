package com.howtodoinjava.demo.batch.quartz.config;

import com.howtodoinjava.demo.batch.quartz.quartzJobs.CustomQuartzJob;
import org.quartz.*;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

  @Autowired
  private JobLauncher jobLauncher;

  @Autowired
  private JobLocator jobLocator;

  @Bean
  public JobDetail jobDetail() {
    //Set Job data map
    JobDataMap jobDataMap = new JobDataMap();
    jobDataMap.put("jobName", "customJob");

    return JobBuilder.newJob(CustomQuartzJob.class)
        .withIdentity("customJob")
        .setJobData(jobDataMap)
        .storeDurably()
        .build();
  }

  @Bean
  public Trigger jobTrigger() {
    SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder
        .simpleSchedule()
        .withIntervalInSeconds(10)
        .repeatForever();

    return TriggerBuilder
        .newTrigger()
        .forJob(jobDetail())
        .withIdentity("jobTrigger")
        .withSchedule(scheduleBuilder)
        .build();
  }
}