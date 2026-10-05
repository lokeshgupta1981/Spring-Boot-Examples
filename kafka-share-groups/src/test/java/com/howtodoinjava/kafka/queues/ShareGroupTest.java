package com.howtodoinjava.kafka.queues;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.ShareGroupDescription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;

@SpringBootTest
@Testcontainers
class ShareGroupTest {

  @Container
  @ServiceConnection
  static KafkaContainer kafka = new KafkaContainer("apache/kafka:4.3.1")
      // a single broker cannot hold the default 3 replicas of the __share_group_state topic
      .withEnv("KAFKA_SHARE_COORDINATOR_STATE_TOPIC_REPLICATION_FACTOR", "1")
      .withEnv("KAFKA_SHARE_COORDINATOR_STATE_TOPIC_MIN_ISR", "1");

  @Autowired KafkaTemplate<String, String> kafkaTemplate;
  @Autowired KafkaAdmin kafkaAdmin;
  @Autowired ThumbnailWorker worker;

  @BeforeEach
  void bothConsumersJoined() throws Exception {
    try (Admin admin = Admin.create(kafkaAdmin.getConfigurationProperties())) {
      await().atMost(Duration.ofSeconds(30)).until(() -> {
        ShareGroupDescription group = admin.describeShareGroups(List.of(QueueConfig.SHARE_GROUP))
            .describedGroups().get(QueueConfig.SHARE_GROUP).get();
        return group.members().size() == 2 && group.members().stream()
            .allMatch(m -> !m.assignment().topicPartitions().isEmpty());
      });
      ShareGroupDescription group = admin.describeShareGroups(List.of(QueueConfig.SHARE_GROUP))
          .describedGroups().get(QueueConfig.SHARE_GROUP).get();
      System.out.println("Share group " + group.groupId() + " state=" + group.groupState()
          + " members=" + group.members().size());
      group.members().forEach(m -> System.out.println("  member " + m.clientId()
          + " partitions=" + m.assignment().topicPartitions()));
    }
  }

  @Test
  void twoConsumersSplitTheRecordsOfOnePartition() {
    IntStream.rangeClosed(1, 10).forEach(i -> kafkaTemplate.send(QueueConfig.TOPIC, "photo-" + i + ".jpg"));

    await().atMost(Duration.ofSeconds(30)).until(() -> worker.doneCount("photo-") == 10);

    Map<String, List<String>> done = worker.doneByConsumer();
    done.forEach((consumer, photos) -> System.out.println(consumer + " -> " + photos));

    assertThat(done).hasSize(2);   // both consumers made thumbnails
    assertThat(done.values()).allSatisfy(photos -> assertThat(photos).isNotEmpty());
    assertThat(done.values().stream().flatMap(List::stream).filter(p -> p.startsWith("photo-")))
        .hasSize(10).doesNotHaveDuplicates();                     // each photo processed once
  }

  @Test
  void releasedRecordComesBackAndRejectedRecordDoesNot() {
    kafkaTemplate.send(QueueConfig.TOPIC, "big.jpg");
    kafkaTemplate.send(QueueConfig.TOPIC, "notes.txt");

    await().atMost(Duration.ofSeconds(30)).until(() -> worker.doneByConsumer().values().stream()
        .anyMatch(photos -> photos.contains("big.jpg")) && worker.rejected().contains("notes.txt"));
    await().during(Duration.ofSeconds(3)).atMost(Duration.ofSeconds(5)).until(() -> true);

    System.out.println("big.jpg attempts=" + worker.attempts("big.jpg")
        + ", notes.txt attempts=" + worker.attempts("notes.txt"));
    assertThat(worker.attempts("big.jpg")).isEqualTo(2);         // released once, accepted on the 2nd delivery
    assertThat(worker.attempts("notes.txt")).isEqualTo(1);       // rejected, never delivered again
  }
}
