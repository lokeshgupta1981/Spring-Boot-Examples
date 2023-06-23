package com.howtodoinjava.demo.jms.service;

import com.howtodoinjava.demo.web.model.TaskStatus;
import lombok.extern.java.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Log
@KafkaListener(topics = {"general-task-topic"}, groupId = "task-group")
public class KafKaTopicListeners {

  private final Logger logger = LoggerFactory.getLogger(KafKaTopicListeners.class);

  @KafkaHandler
  public void consume(TaskStatus taskStatus) {

    logger.info(String.format("Task status is updated : " + taskStatus));
  }
}
