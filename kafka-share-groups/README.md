Source code for the article https://howtodoinjava.com/?p=44239

# Kafka Queues (Share Groups, KIP-932) with Spring Kafka

A thumbnail worker that reads photo names from a Kafka topic with ONE partition. Two consumers in the
share group `thumbnail-workers` split the records of that partition, and the listener acknowledges each
record on its own:

- `acknowledge()` (ACCEPT) for a photo it resized
- `release()` (RELEASE) for `big.jpg` on the first try, so Kafka delivers it again
- `reject()` (REJECT) for anything that is not a `.jpg`, so Kafka never delivers it again

## Versions

- Spring Boot 4.1.1 (Spring Kafka 4.1.1, kafka-clients 4.2.1, JUnit 6.0.3, Testcontainers 2.0.5)
- Apache Kafka broker 4.3.1 (Docker image `apache/kafka:4.3.1`)
- Java 25
- Maven 3.9+
- Docker (for the tests and for running the app)

## Files

| File | What it shows |
|---|---|
| `QueueConfig.java` | `ShareConsumerFactory`, `ShareKafkaListenerContainerFactory` (MANUAL acks, concurrency 2) and the one-partition topic |
| `ShareGroupSetup.java` | sets `share.auto.offset.reset=earliest` on the share group before the listeners start |
| `ThumbnailWorker.java` | `@KafkaListener` with `ShareAcknowledgment` (acknowledge, release, reject) |
| `ShareGroupTest.java` | Spring Boot test with Testcontainers: two consumers split one partition; release and reject |
| `PlainShareConsumerTest.java` | the same queue with the plain `KafkaShareConsumer` client, without Spring |

## Run the tests

```bash
mvn test
```

3 tests. Sample output:

```text
Share group thumbnail-workers state=Stable members=2
  member thumbnailWorkers-0 partitions=[thumbnails-0]
  member thumbnailWorkers-1 partitions=[thumbnails-0]
thumbnailWorkers-C-1 -> [photo-2.jpg, photo-3.jpg, photo-5.jpg, photo-7.jpg, photo-9.jpg]
thumbnailWorkers-C-2 -> [photo-1.jpg, photo-4.jpg, photo-6.jpg, photo-8.jpg, photo-10.jpg]
big.jpg attempts=2, notes.txt attempts=1
```

## Run the app

Start a single Kafka 4.3.1 broker. Its default `server.properties` already sets the replication factor
of the `__share_group_state` topic to 1, which a single broker needs.

```bash
docker run -d --name kafka-queues -p 9092:9092 apache/kafka:4.3.1
mvn spring-boot:run
```

Wait until both consumers have joined (a few seconds), then send some photo names from another terminal.

```bash
printf 'photo-1.jpg\nphoto-2.jpg\nphoto-3.jpg\nphoto-4.jpg\nbig.jpg\nnotes.txt\n' | \
  docker exec -i kafka-queues /opt/kafka/bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic thumbnails
```

The app log shows both consumers taking records from the same partition.

```text
thumbnailWorkers-C-1 ACCEPT  photo-1.jpg (offset 0) attempt 1
thumbnailWorkers-C-2 ACCEPT  photo-2.jpg (offset 1) attempt 1
thumbnailWorkers-C-1 ACCEPT  photo-3.jpg (offset 2) attempt 1
thumbnailWorkers-C-2 ACCEPT  photo-4.jpg (offset 3) attempt 1
thumbnailWorkers-C-1 RELEASE big.jpg (offset 4) image service busy, attempt 1
thumbnailWorkers-C-2 REJECT  notes.txt (offset 5) not an image
thumbnailWorkers-C-1 ACCEPT  big.jpg (offset 4) attempt 2
```

Check the share group members.

```bash
docker exec kafka-queues /opt/kafka/bin/kafka-share-groups.sh --bootstrap-server localhost:9092 \
  --describe --group thumbnail-workers --members
```
