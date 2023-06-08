package com.howtodoinjava.demo.web.service;

import com.howtodoinjava.demo.web.model.TaskRequest;
import com.howtodoinjava.demo.web.model.TaskStatus;
import com.howtodoinjava.demo.web.model.TaskStatus.Status;
import java.util.concurrent.CompletableFuture;
import lombok.extern.java.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Log
@Service
public class TaskService {

  @Autowired
  KafkaTemplate<String, TaskStatus> kafkaTemplate;

  final static Logger LOGGER = LoggerFactory.getLogger(TaskService.class);

  @Async
  public void process(String taskId, TaskRequest taskRequest) {

    try {
      updateTaskExecutionProgess(new TaskStatus(taskId, taskRequest.getName(), 0.0f, Status.SUBMITTED));

      Thread.currentThread().sleep(2000l);
      updateTaskExecutionProgess(new TaskStatus(taskId, taskRequest.getName(), 10.0f, Status.STARTED));

      Thread.currentThread().sleep(5000l);
      updateTaskExecutionProgess(new TaskStatus(taskId, taskRequest.getName(), 50.0f, Status.RUNNING));

      Thread.currentThread().sleep(5000l);
      updateTaskExecutionProgess(new TaskStatus(taskId, taskRequest.getName(), 100.0f, Status.FINISHED));

    } catch (InterruptedException e) {
      updateTaskExecutionProgess(new TaskStatus(taskId, taskRequest.getName(), 100.0f, Status.TERMINATED));
      throw new RuntimeException(e);
    }
  }

  void updateTaskExecutionProgess(TaskStatus taskStatus) {

    try {
      CompletableFuture<SendResult<String, TaskStatus>> future
          = kafkaTemplate.send("task-topic", taskStatus.getTaskId(), taskStatus);

      future.whenComplete((sendResult, exception) -> {
        if (exception != null) {
          future.completeExceptionally(exception);
        } else {
          future.complete(sendResult);
        }
        LOGGER.info("Task status send to Kafka topic : "+ taskStatus);
      });
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}
