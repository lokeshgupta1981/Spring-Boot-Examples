package com.howtodoinjava.demo.batch.jobs.csvToDb.tasklets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.UnexpectedJobExecutionException;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

import java.io.File;

public class DeleteInputCsvTasklet implements Tasklet, InitializingBean {

  private static final Logger log = LoggerFactory.getLogger(DeleteInputCsvTasklet.class);

  private Resource[] resources;

  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

    for (Resource r : resources) {
      File file = r.getFile();
      boolean deleted = file.delete();
      if (!deleted) {
        throw new UnexpectedJobExecutionException("Could not delete file " + file.getPath());
      }
    }
    return RepeatStatus.FINISHED;
  }

  public void setResources(Resource[] resources) {
    this.resources = resources;
  }

  public void afterPropertiesSet() throws Exception {
    if (resources == null) {
      log.info("No resource to delete");
    }
  }
}
