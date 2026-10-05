package com.howtodoinjava.demo.batch.jobs.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.jdbc.core.JdbcTemplate;

public class BookImportJobListener implements JobExecutionListener {

  private static final Logger log = LoggerFactory.getLogger(BookImportJobListener.class);

  private final JdbcTemplate jdbcTemplate;

  public BookImportJobListener(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public void afterJob(JobExecution jobExecution) {
    if (jobExecution.getStatus() != BatchStatus.COMPLETED) {
      log.warn("Job ended with status {}", jobExecution.getStatus());
      return;
    }
    for (StepExecution step : jobExecution.getStepExecutions()) {
      log.info("Step {}: read={}, filtered={}, skipped={}, written={}",
          step.getStepName(), step.getReadCount(), step.getFilterCount(),
          step.getSkipCount(), step.getWriteCount());
    }
    jdbcTemplate.query("select title, author, pages, price from book order by id",
        (rs, row) -> rs.getString("title") + " by " + rs.getString("author")
            + " (" + rs.getInt("pages") + " pages, " + rs.getDouble("price") + ")")
        .forEach(line -> log.info("In database: {}", line));
  }
}
