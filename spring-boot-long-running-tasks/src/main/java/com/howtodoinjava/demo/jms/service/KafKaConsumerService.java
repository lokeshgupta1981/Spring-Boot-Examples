package com.howtodoinjava.demo.jms.service;

import com.howtodoinjava.demo.web.model.TaskStatus;
import lombok.extern.java.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Log
public class KafKaConsumerService {

  private final Logger logger = LoggerFactory.getLogger(KafKaConsumerService.class);

  @KafkaListener(topics = {"task-topic"}, groupId = "task-group")
  public void consume(TaskStatus taskStatus) {

    logger.info(String.format("Task status is updated : " + taskStatus));
  }
}
