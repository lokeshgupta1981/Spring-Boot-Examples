package producer;

import consumer.MyKafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static constants.GlobalConstants.TOPIC;

public class TestMyKafkaConsumer
{
    @Test
    void addRecordToTheConsumer_verifyMessageReceived_andConsumerClosed() throws InterruptedException
    {
        try (MockConsumer<String, String> mockConsumer = new MockConsumer<>(OffsetResetStrategy.EARLIEST))
        {
            var records = new ConcurrentHashMap<Long, ConsumerRecord<String, String>>();

            MyKafkaConsumer kafkaConsumer = new MyKafkaConsumer(mockConsumer, records);

            // start consumer polling
            Thread consumerThread = new Thread(()->kafkaConsumer.startPolling(TOPIC, 1));

            consumerThread.start();

            mockConsumer.updateBeginningOffsets(Map.of(new TopicPartition(TOPIC, 1), 0L));

            mockConsumer.schedulePollTask(()-> mockConsumer.addRecord(new ConsumerRecord<>(TOPIC, 1, 0, "Hello", "Hello World")));

            TimeUnit.MILLISECONDS.sleep(200); // wait for consumer to poll

            Assertions.assertEquals(1, records.size());

            mockConsumer.schedulePollTask(mockConsumer::close);

            TimeUnit.MILLISECONDS.sleep(200); // wait for consumer to poll

            Assertions.assertTrue(mockConsumer.closed());

        }
    }

    @Test
    void addRecordToTheConsumerWrongPartition_verifyNoMessageReceived() throws InterruptedException
    {
        try (MockConsumer<String, String> mockConsumer = new MockConsumer<>(OffsetResetStrategy.EARLIEST))
        {
            var records = new ConcurrentHashMap<Long, ConsumerRecord<String, String>>();

            MyKafkaConsumer kafkaConsumer = new MyKafkaConsumer(mockConsumer, records);

            // start consumer polling
            Thread consumerThread = new Thread(()->kafkaConsumer.startPolling(TOPIC, 1));

            consumerThread.start();

            mockConsumer.updateBeginningOffsets(Map.of(new TopicPartition(TOPIC, 1), 0L));

            mockConsumer.schedulePollTask(()-> mockConsumer.addRecord(new ConsumerRecord<>(TOPIC, 0, 0, "Hello", "Hello World")));

            TimeUnit.MILLISECONDS.sleep(200); // wait for consumer to poll

            Assertions.assertEquals(0, records.size());

            mockConsumer.schedulePollTask(mockConsumer::close);

            TimeUnit.MILLISECONDS.sleep(200); // wait for consumer to poll

            Assertions.assertTrue(mockConsumer.closed());

        }
    }

    @Test
    void addRecordToTheConsumerWrongTopic_verifyNoMessageReceived() throws InterruptedException
    {
        try (MockConsumer<String, String> mockConsumer = new MockConsumer<>(OffsetResetStrategy.EARLIEST))
        {
            var records = new ConcurrentHashMap<Long, ConsumerRecord<String, String>>();

            MyKafkaConsumer kafkaConsumer = new MyKafkaConsumer(mockConsumer, records);

            // start consumer polling
            Thread consumerThread = new Thread(()->kafkaConsumer.startPolling(TOPIC, 1));

            consumerThread.start();

            mockConsumer.updateBeginningOffsets(Map.of(new TopicPartition(TOPIC, 1), 0L));

            mockConsumer.schedulePollTask(()-> mockConsumer.addRecord(new ConsumerRecord<>("ABCD", 1, 0, "Hello", "Hello World")));

            TimeUnit.MILLISECONDS.sleep(200); // wait for consumer to poll

            Assertions.assertEquals(0, records.size());

            mockConsumer.schedulePollTask(mockConsumer::close);

            TimeUnit.MILLISECONDS.sleep(200); // wait for consumer to poll

            Assertions.assertTrue(mockConsumer.closed());

        }
    }

    @Test
    void closeConsumer_usingStopPolling() throws InterruptedException
    {
        try (MockConsumer<String, String> mockConsumer = new MockConsumer<>(OffsetResetStrategy.EARLIEST))
        {
            var records = new ConcurrentHashMap<Long, ConsumerRecord<String, String>>();

            MyKafkaConsumer kafkaConsumer = new MyKafkaConsumer(mockConsumer, records);

            // start consumer polling
            Thread consumerThread = new Thread(()->kafkaConsumer.startPolling(TOPIC, 1));

            consumerThread.start();

            mockConsumer.updateBeginningOffsets(Map.of(new TopicPartition(TOPIC, 1), 0L));

            mockConsumer.schedulePollTask(()-> mockConsumer.addRecord(new ConsumerRecord<>(TOPIC, 1, 0, "Hello", "Hello World")));

            TimeUnit.MILLISECONDS.sleep(200); // wait for consumer to poll

            Assertions.assertEquals(1, records.size());

            kafkaConsumer.stopPolling();

            Assertions.assertTrue(mockConsumer.closed());

        }
    }
}
