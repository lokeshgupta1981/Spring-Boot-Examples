package com.howtodoinjava.kafka.queues;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.AlterConfigOp;
import org.apache.kafka.clients.admin.ConfigEntry;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.AcknowledgeType;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaShareConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.config.ConfigResource;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;

/** The same queue with the plain Kafka client, without Spring. */
@Testcontainers
class PlainShareConsumerTest {

  static final String TOPIC = "plain-thumbnails";
  static final String GROUP = "plain-workers";

  @Container
  static KafkaContainer kafka = new KafkaContainer("apache/kafka:4.3.1")
      .withEnv("KAFKA_SHARE_COORDINATOR_STATE_TOPIC_REPLICATION_FACTOR", "1")
      .withEnv("KAFKA_SHARE_COORDINATOR_STATE_TOPIC_MIN_ISR", "1");

  @Test
  void twoPlainShareConsumersSplitOnePartition() throws Exception {
    try (Admin admin = Admin.create(Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers()))) {
      admin.createTopics(List.of(new NewTopic(TOPIC, 1, (short) 1))).all().get();
      var group = new ConfigResource(ConfigResource.Type.GROUP, GROUP);
      var earliest = new AlterConfigOp(new ConfigEntry("share.auto.offset.reset", "earliest"), AlterConfigOp.OpType.SET);
      admin.incrementalAlterConfigs(Map.of(group, List.of(earliest))).all().get();
    }

    Map<String, List<String>> done = new ConcurrentHashMap<>();
    AtomicBoolean running = new AtomicBoolean(true);
    try (var pool = Executors.newFixedThreadPool(2)) {
      pool.submit(() -> consume("consumer-a", done, running));
      pool.submit(() -> consume("consumer-b", done, running));

      try (var producer = new KafkaProducer<String, String>(Map.of(
          ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers(),
          ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
          ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class))) {
        for (int i = 1; i <= 10; i++) {
          producer.send(new ProducerRecord<>(TOPIC, "photo-" + i + ".jpg"));
        }
        producer.send(new ProducerRecord<>(TOPIC, "notes.txt"));
      }

      long deadline = System.nanoTime() + Duration.ofSeconds(30).toNanos();
      while (total(done) < 11 && System.nanoTime() < deadline) {
        LockSupport.parkNanos(Duration.ofMillis(100).toNanos());
      }
      running.set(false);
    }

    done.forEach((consumer, photos) -> System.out.println(consumer + " -> " + photos));
    assertThat(done).hasSize(2);
    assertThat(done.values().stream().flatMap(List::stream)).hasSize(11).doesNotHaveDuplicates();
  }

  private static void consume(String name, Map<String, List<String>> done, AtomicBoolean running) {
    String bootstrapServers = kafka.getBootstrapServers();
    Map<String, Object> props = Map.of(
        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
        ConsumerConfig.GROUP_ID_CONFIG, GROUP,
        ConsumerConfig.CLIENT_ID_CONFIG, name,
        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
        ConsumerConfig.SHARE_ACKNOWLEDGEMENT_MODE_CONFIG, "explicit",
        ConsumerConfig.SHARE_ACQUIRE_MODE_CONFIG, "record_limit",
        ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1);

    try (var consumer = new KafkaShareConsumer<String, String>(props)) {
      consumer.subscribe(List.of(TOPIC));
      while (running.get()) {
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500));
        for (ConsumerRecord<String, String> record : records) {
          AcknowledgeType result = record.value().endsWith(".jpg")
              ? AcknowledgeType.ACCEPT : AcknowledgeType.REJECT;   // ACCEPT for "photo-1.jpg"
          consumer.acknowledge(record, result);
          LockSupport.parkNanos(Duration.ofMillis(200).toNanos());
          done.computeIfAbsent(name, k -> new CopyOnWriteArrayList<>()).add(record.value() + "=" + result);
        }
        consumer.commitSync();                                     // sends the acknowledgements
      }
    }
  }

  private static int total(Map<String, List<String>> done) {
    return done.values().stream().mapToInt(List::size).sum();
  }
}
