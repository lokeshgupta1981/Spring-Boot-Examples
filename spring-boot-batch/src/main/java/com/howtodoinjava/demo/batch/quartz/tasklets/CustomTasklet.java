package com.howtodoinjava.demo.batch.quartz.tasklets;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
public class CustomTasklet implements Tasklet {

  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
    System.out.println("CustomTasklet start..");

    // ... your code

    System.out.println("CustomTasklet done..");
    return RepeatStatus.FINISHED;
  }
}