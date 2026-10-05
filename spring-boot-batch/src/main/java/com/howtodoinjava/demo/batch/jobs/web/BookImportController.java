package com.howtodoinjava.demo.batch.jobs.web;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class BookImportController {

  private final JobOperator jobOperator;
  private final Job importBooksJob;

  public BookImportController(JobOperator jobOperator, Job importBooksJob) {
    this.jobOperator = jobOperator;
    this.importBooksJob = importBooksJob;
  }

  @PostMapping("/books/import")
  public String importBooks(@RequestParam(defaultValue = "books.csv") String file)
      throws Exception {
    JobParameters params = new JobParametersBuilder()
        .addString("inputFile", file)
        .addLocalDateTime("requestedAt", LocalDateTime.now())   // makes every run a new instance
        .toJobParameters();
    JobExecution execution = jobOperator.start(importBooksJob, params);
    return "Job " + execution.getId() + " finished with status " + execution.getStatus();
  }
}
