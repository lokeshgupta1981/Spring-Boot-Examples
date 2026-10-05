package com.howtodoinjava.demo.batch.jobs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.batch.test.JobOperatorTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = "spring.batch.job.enabled=false")
@SpringBatchTest
class BookImportJobTest {

  @Autowired
  private JobOperatorTestUtils jobOperatorTestUtils;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private JobOperator jobOperator;

  @Autowired
  private JobRepository jobRepository;

  @BeforeEach
  void cleanTable() {
    jdbcTemplate.update("delete from book");
  }

  @Test
  void importsBooksFromCsv() throws Exception {
    JobParameters params = new JobParametersBuilder()
        .addString("inputFile", "books.csv")
        .addString("run", "test-1")
        .toJobParameters();

    JobExecution execution = jobOperatorTestUtils.startJob(params);

    assertEquals(BatchStatus.COMPLETED, execution.getStatus());
    StepExecution step = execution.getStepExecutions().iterator().next();
    assertEquals(6, step.getReadCount());      // 7 lines, 1 skipped
    assertEquals(1, step.getFilterCount());    // no author
    assertEquals(1, step.getSkipCount());      // pages = abc
    assertEquals(5, step.getWriteCount());
    Integer rows = jdbcTemplate.queryForObject("select count(*) from book", Integer.class);
    assertEquals(5, rows);
  }

  @Test
  void sameParametersCannotRunTwice() throws Exception {
    JobParameters params = new JobParametersBuilder()
        .addString("inputFile", "books.csv")
        .addString("run", "test-2")
        .toJobParameters();
    jobOperatorTestUtils.startJob(params);

    assertThrows(JobInstanceAlreadyCompleteException.class,
        () -> jobOperatorTestUtils.startJob(params));
  }

  @Test
  void restartCreatesNewExecutionOfSameInstance() throws Exception {
    JobParameters params = new JobParametersBuilder()
        .addString("inputFile", "missing.csv")
        .addString("run", "test-4")
        .toJobParameters();
    JobExecution failedExecution = jobOperatorTestUtils.startJob(params);
    assertEquals(BatchStatus.FAILED, failedExecution.getStatus());

    Long restartedId = jobOperator.restart(failedExecution.getId());   // continues after the last committed chunk

    JobExecution restarted = jobRepository.getJobExecution(restartedId);
    assertNotEquals(failedExecution.getId(), restartedId);
    assertEquals(failedExecution.getJobInstance().getId(), restarted.getJobInstance().getId());
    assertEquals(BatchStatus.FAILED, restarted.getStatus());   // the file is still missing
  }

  @Test
  void storesMetadataInBatchTables() throws Exception {
    JobParameters params = new JobParametersBuilder()
        .addString("inputFile", "books.csv")
        .addString("run", "test-3")
        .toJobParameters();
    jobOperatorTestUtils.startJob(params);

    Integer instances = jdbcTemplate.queryForObject(
        "select count(*) from batch_job_instance where job_name = 'importBooksJob'", Integer.class);
    assertTrue(instances >= 1);
  }
}
