package producer;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.Future;

public class MyKafkaProducer
{

    private final Producer<String, String> producer;

    public MyKafkaProducer(Producer<String, String> producer)
    {
        this.producer = producer;
    }

    public Future<RecordMetadata> send(String topic, String key, String data)
    {
        try
        {
            ProducerRecord<String, String> message = new ProducerRecord<>(topic, key, data);

            return producer.send(message);
        }
        catch (Exception e)
        {
            System.out.println(e);

            throw new RuntimeException(e);
        }

    }

    public static Producer<String, String> getProducer()
    {
        Properties properties = new Properties();

        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        return new KafkaProducer<>(properties);
    }

}
