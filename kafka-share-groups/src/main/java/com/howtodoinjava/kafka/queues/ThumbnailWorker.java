package com.howtodoinjava.kafka.queues;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.LockSupport;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.ShareAcknowledgment;
import org.springframework.stereotype.Component;

@Component
public class ThumbnailWorker {

  private static final Logger log = LoggerFactory.getLogger(ThumbnailWorker.class);

  // which consumer thread made which thumbnail
  private final Map<String, List<String>> doneByConsumer = new ConcurrentHashMap<>();
  private final List<String> rejected = new CopyOnWriteArrayList<>();
  private final Map<String, Integer> attempts = new ConcurrentHashMap<>();

  @KafkaListener(id = "thumbnailWorkers", topics = QueueConfig.TOPIC,
      groupId = QueueConfig.SHARE_GROUP, containerFactory = "shareListenerFactory")
  public void makeThumbnail(ConsumerRecord<String, String> record, ShareAcknowledgment ack) {
    String photo = record.value();
    String consumer = Thread.currentThread().getName();
    int attempt = attempts.merge(photo, 1, Integer::sum);

    if (!photo.endsWith(".jpg")) {
      log.info("{} REJECT  {} (offset {}) not an image", consumer, photo, record.offset());
      rejected.add(photo);
      ack.reject();          // REJECT: "notes.txt" is never delivered again
      return;
    }
    if (photo.startsWith("big") && attempt == 1) {
      log.info("{} RELEASE {} (offset {}) image service busy, attempt {}", consumer, photo, record.offset(), attempt);
      ack.release();         // RELEASE: "big.jpg" is delivered again
      return;
    }

    resize(photo);
    doneByConsumer.computeIfAbsent(consumer, k -> new CopyOnWriteArrayList<>()).add(photo);
    log.info("{} ACCEPT  {} (offset {}) attempt {}", consumer, photo, record.offset(), attempt);
    ack.acknowledge();       // ACCEPT: the record is done
  }

  private void resize(String photo) {
    LockSupport.parkNanos(Duration.ofMillis(200).toNanos());  // pretend to resize the image
  }

  public Map<String, List<String>> doneByConsumer() {
    return doneByConsumer;
  }

  public List<String> rejected() {
    return rejected;
  }

  public int attempts(String photo) {
    return attempts.getOrDefault(photo, 0);
  }

  public long doneCount(String prefix) {
    return doneByConsumer.values().stream().flatMap(List::stream).filter(p -> p.startsWith(prefix)).count();
  }
}
