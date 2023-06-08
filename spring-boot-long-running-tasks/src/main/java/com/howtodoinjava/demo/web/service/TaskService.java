package com.howtodoinjava.demo.web.service;

import com.howtodoinjava.demo.web.model.TaskRequest;
import com.howtodoinjava.demo.web.model.TaskStatus;
import com.howtodoinjava.demo.web.model.TaskStatus.Status;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import lombok.extern.java.Log;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.config.TopicConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Log
@Service
public class TaskService {

  @Autowired
  KafkaTemplate<String, TaskStatus> kafkaTemplate;

  @Autowired
  KafkaConsumer<String, TaskStatus> kafkaConsumer;

  @Autowired
  KafkaAdmin kafkaAdmin;

  final static Logger LOGGER = LoggerFactory.getLogger(TaskService.class);

  @Async
  public void process(String taskId, TaskRequest taskRequest) {

    try {
      createNewTopic(taskId);

      updateTaskExecutionProgess(new TaskStatus(taskId, taskRequest.getName(), 0.0f, Status.SUBMITTED));

      Thread.currentThread().sleep(2000l);
      updateTaskExecutionProgess(new TaskStatus(taskId, taskRequest.getName(), 10.0f, Status.STARTED));

      Thread.currentThread().sleep(5000l);
      updateTaskExecutionProgess(new TaskStatus(taskId, taskRequest.getName(), 50.0f, Status.RUNNING));

      Thread.currentThread().sleep(5000l);
      updateTaskExecutionProgess(new TaskStatus(taskId, taskRequest.getName(), 100.0f, Status.FINISHED));

    } catch (InterruptedException | ExecutionException e) {
      updateTaskExecutionProgess(new TaskStatus(taskId, taskRequest.getName(), 100.0f, Status.TERMINATED));
      throw new RuntimeException(e);
    }
  }

  private void createNewTopic(String topicName) throws ExecutionException, InterruptedException {

    Map<String, String> topicConfig = new HashMap<>();
    topicConfig.put(TopicConfig.RETENTION_MS_CONFIG, String.valueOf(24 * 60 * 60 * 1000)); // 24 hours retention
    NewTopic newTopic = new NewTopic(topicName, 1, (short) 1).configs(topicConfig);
    try (AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
      adminClient.createTopics(Collections.singletonList(newTopic)).all().get();
    }
    kafkaConsumer.subscribe(Collections.singletonList(topicName));
  }

  void updateTaskExecutionProgess(TaskStatus taskStatus) {

    try {
      CompletableFuture<SendResult<String, TaskStatus>> future
          = kafkaTemplate.send(taskStatus.getTaskId(), taskStatus.getTaskId(), taskStatus);

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

  //For client application
  public TaskStatus getLatestTaskStatus(String taskId){
    ConsumerRecord<String, TaskStatus> latestUpdate = null;
    ConsumerRecords<String, TaskStatus> consumerRecords = kafkaConsumer.poll(Duration.ofMillis(1000));
    if (!consumerRecords.isEmpty()) {
      Iterator itr = consumerRecords.records(taskId).iterator();
      while(itr.hasNext()) {
        latestUpdate = (ConsumerRecord<String, TaskStatus>) itr.next();
      }
      LOGGER.info("Latest updated status : "+ latestUpdate.value());
    }
    return latestUpdate != null ? latestUpdate.value() : null;
  }
}
