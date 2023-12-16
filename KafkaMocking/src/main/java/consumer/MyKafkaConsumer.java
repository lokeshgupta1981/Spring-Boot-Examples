package consumer;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import constants.GlobalConstants;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class MyKafkaConsumer
{
    private final Consumer<String, String> consumer;

    private ConcurrentHashMap<Long, ConsumerRecord<String, String>> records;

    private static final AtomicBoolean HAS_MORE_MESSAGES = new AtomicBoolean(true);

    public MyKafkaConsumer(Consumer<String, String> consumer)
    {
        this.consumer = consumer;
    }

    public MyKafkaConsumer(Consumer<String, String> consumer, ConcurrentHashMap<Long, ConsumerRecord<String, String>> records)
    {
        this.consumer = consumer;
        this.records = records;
    }

    public static Consumer<String, String> getConsumer()
    {
        Properties properties = new Properties();

        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, GlobalConstants.BOOTSTRAP_SERVER);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, GlobalConstants.GROUP_ID);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, OffsetResetStrategy.EARLIEST.toString());

        return new KafkaConsumer<>(properties);
    }

    public void startPolling(String topic)
    {
        consumer.subscribe(List.of(GlobalConstants.TOPIC));

        startPolling();
    }

    public void startPolling(String topic, int partition)
    {
        consumer.assign(List.of(new TopicPartition(topic, partition)));

        startPolling();
    }

    private void startPolling()
    {
        try(consumer)
        {
            while (HAS_MORE_MESSAGES.get())
            {
                ConsumerRecords<String, String> consumerRecord = consumer.poll(Duration.ofMillis(300));

                consumerRecord.forEach(record -> {
                    System.out.println("---------------------Message Received---------------------");
                    System.out.println("Topic: "+ record.topic());
                    System.out.println("Key: "+ record.key());
                    System.out.println("Value: "+ record.value());
                    System.out.println("Partition: "+ record.partition());
                    System.out.println("Offset: "+ record.offset());

                    if (records != null)
                    {
                        // offset is always unique and incremental
                        records.put(record.offset(), record);
                    }

                });

                // commits the offset
                consumer.commitAsync();
            }
        }
        catch (WakeupException ignored)
        {

        }
        catch (Exception exception)
        {
            System.out.println(exception.getMessage());
        }
    }

    public void stopPolling()
    {
        System.out.println("Stopping Consumer");

        consumer.wakeup();

        HAS_MORE_MESSAGES.set(false);
    }

}
