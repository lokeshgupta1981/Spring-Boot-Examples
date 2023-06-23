package com.howtodoinjava.demo;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.howtodoinjava.demo.web.model.TaskStatus;
import com.howtodoinjava.demo.web.model.TaskStatus.Status;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

//@SpringBootTest
public class LongRunningTaskApplicationTest {

  /*@Autowired
  private KafkaConsumer consumer;

  @Autowired
  private KafkaProducer producer;

  @Test
  void testMessageProducer(){
    producer.send(new ProducerRecord("task-topic", new TaskStatus("task-id", "task-name", 50.0f, Status.RUNNING)));

    ConsumerRecords consumerRecords = consumer.poll(5000l);
    assertTrue(consumerRecords.count() == 1);
    consumerRecords.forEach(ts -> assertTrue(ts != null));
  }*/
}
