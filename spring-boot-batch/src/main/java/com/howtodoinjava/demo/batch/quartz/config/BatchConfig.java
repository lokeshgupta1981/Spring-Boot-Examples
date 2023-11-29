package com.howtodoinjava.demo.batch.quartz.config;

import com.howtodoinjava.demo.batch.quartz.tasklets.CustomTasklet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

  JobRepository jobRepository;
  PlatformTransactionManager txManager;

  public BatchConfig(JobRepository jobRepository, PlatformTransactionManager txManager) {
    this.jobRepository = jobRepository;
    this.txManager = txManager;
  }

  @Bean
  public Step step1(CustomTasklet customTasklet) {

    var name = "step1";
    var builder = new StepBuilder(name, jobRepository);
    return builder.tasklet(customTasklet, txManager).build();
  }

  @Bean
  public Job customJob(Step step1) {
    var name = "customJob";
    var builder = new JobBuilder(name, jobRepository);
    return builder.start(step1).build();
  }
}
