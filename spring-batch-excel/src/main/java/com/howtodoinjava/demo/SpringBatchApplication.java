package com.howtodoinjava.demo;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDateTime;

@SpringBootApplication
@EnableScheduling
public class SpringBatchApplication implements CommandLineRunner {

  @Autowired
  @Qualifier("excelFileToDatabaseJob")
  Job job;

  @Autowired
  JobLauncher jobLauncher;

  public static void main(String[] args) {
    SpringApplication.run(SpringBatchApplication.class);
  }

  @Override
  public void run(String... args) throws Exception {
    JobParameters jobParameters = new JobParametersBuilder()
        .addJobParameter("currentTimestamp", new JobParameter(LocalDateTime.now(), LocalDateTime.class))
        .toJobParameters();

    jobLauncher.run(job, jobParameters);
  }
}
