package com.howtodoinjava.demo;

import com.howtodoinjava.demo.web.model.TaskStatus;
import com.howtodoinjava.demo.web.model.TaskStatus.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.kafka.core.KafkaTemplate;

@SpringBootApplication
public class LongRunningTaskApplication implements CommandLineRunner {

  @Autowired
  KafkaTemplate kafkaTemplate;

  @Autowired
  KafkaProperties kafkaProperties;

  public static void main(String[] args) {
    SpringApplication.run(LongRunningTaskApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    kafkaTemplate.send("general-task-topic", 0, "taskId", new TaskStatus("taskId", "taskName", 50.0f, Status.RUNNING));
   // kafkaTemplate.send("general-task-topic", 0, "taskId", new TaskStatus("taskId", "taskName", 100.0f, Status.FINISHED));

    kafkaTemplate.send("general-task-topic", "test-message");

    /*var message1 = kafkaTemplate.receive("general-task-topic", 0, 0L);
    var message2 = kafkaTemplate.receive("general-task-topic", 0, 1L);

    System.out.println(message1.value());
    System.out.println(message2.value());*/
  }
}
