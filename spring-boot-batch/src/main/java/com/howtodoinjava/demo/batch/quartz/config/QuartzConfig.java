package com.howtodoinjava.demo.batch.quartz.config;

import com.howtodoinjava.demo.batch.quartz.job.CustomQuartzJobBean;
import org.quartz.*;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.io.IOException;
import java.util.Properties;

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
    jobDataMap.put("jobLauncher", jobLauncher);
    jobDataMap.put("jobLocator", jobLocator);

    return JobBuilder.newJob(CustomQuartzJobBean.class)
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

  @Bean
  public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
    SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
    scheduler.setTriggers(jobTrigger());
    scheduler.setQuartzProperties(quartzProperties());
    scheduler.setJobDetails(jobDetail());
    return scheduler;
  }

  @Bean
  public Properties quartzProperties() throws IOException {
    PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
    propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
    propertiesFactoryBean.afterPropertiesSet();
    return propertiesFactoryBean.getObject();
  }
}