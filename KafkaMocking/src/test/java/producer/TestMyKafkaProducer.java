package producer;

import constants.GlobalConstants;
import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.errors.InvalidTopicException;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class TestMyKafkaProducer
{
    @Test
    void sendMessage_verifyTheHistory()
    {
        try (MockProducer<String, String> mockProducer = new MockProducer<>(true, new StringSerializer(), new StringSerializer()))
        {
            MyKafkaProducer kafkaProducer = new MyKafkaProducer(mockProducer);

            kafkaProducer.send(GlobalConstants.TOPIC, "Hello", "Hello World");

            Assertions.assertEquals(1, mockProducer.history().size());
        }
    }

    @Test
    void sendMessage_verifyTheTopic() throws ExecutionException, InterruptedException
    {
        try (MockProducer<String, String> mockProducer = new MockProducer<>(true, new StringSerializer(), new StringSerializer()))
        {
            MyKafkaProducer kafkaProducer = new MyKafkaProducer(mockProducer);

            Future<RecordMetadata> metadata = kafkaProducer.send(GlobalConstants.TOPIC, "Hello", "Hello World");

            Assertions.assertEquals(metadata.get().topic(), mockProducer.history().get(0).topic());
        }
    }

    @Test
    void sendMessageWithDifferentKeys_verifySentToDifferentTopicPartition() throws ExecutionException, InterruptedException
    {
        PartitionInfo partitionInfo0 = new PartitionInfo(GlobalConstants.TOPIC, 0, null, null, null);
        PartitionInfo partitionInfo1 = new PartitionInfo(GlobalConstants.TOPIC, 1, null, null, null);

        List<PartitionInfo> list = new ArrayList<>();

        list.add(partitionInfo0);
        list.add(partitionInfo1);

        Cluster kafkaCluster = new Cluster("id1", new ArrayList<>(), list, Collections.emptySet(), Collections.emptySet());

        try (MockProducer<String, String> mockProducer = new MockProducer<>(kafkaCluster, true, new StringSerializer(), new StringSerializer()))
        {
            MyKafkaProducer kafkaProducer = new MyKafkaProducer(mockProducer);

            Future<RecordMetadata> metadata1 = kafkaProducer.send(GlobalConstants.TOPIC, "New World", "Hello World");

            Future<RecordMetadata> metadata2 = kafkaProducer.send(GlobalConstants.TOPIC, "Hello World", "Hello World");

            Assertions.assertNotEquals(metadata1.get().partition(), metadata2.get().partition());
        }
    }

    @Test
    void sendMessage_raiseException_verifyException()
    {
        try (MockProducer<String, String> mockProducer = new MockProducer<>(false, new StringSerializer(), new StringSerializer()))
        {
            MyKafkaProducer kafkaProducer = new MyKafkaProducer(mockProducer);

            Future<RecordMetadata> metadata = kafkaProducer.send("ABCD", "Hello", "Hello World");

            InvalidTopicException e = new InvalidTopicException();
            mockProducer.errorNext(e);

            try
            {
                metadata.get();
            }
            catch (Exception exception)
            {
                Assertions.assertEquals(e, exception.getCause());
            }
        }
    }

    @Test
    void sendMessage_withTransaction()
    {
        try (MockProducer<String, String> mockProducer = new MockProducer<>(false, new StringSerializer(), new StringSerializer()))
        {
            MyKafkaProducer kafkaProducer = new MyKafkaProducer(mockProducer);

            mockProducer.initTransactions();

            mockProducer.beginTransaction();

            Future<RecordMetadata> metadata = kafkaProducer.send(GlobalConstants.TOPIC, "Hello", "Hello World");

            Assertions.assertFalse(metadata.isDone());

            Assertions.assertEquals(mockProducer.history().size(), 0);

            mockProducer.commitTransaction();

            Assertions.assertEquals(mockProducer.history().size(), 1);

            Assertions.assertTrue(metadata.isDone());
        }
    }
}
