package com.howtodoinjava.avro.example.producer;

import com.howtodoinjava.avro.example.domain.generated.Order;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import io.confluent.kafka.serializers.subject.RecordNameStrategy;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class CoffeeOrderProducerSchemaRegistry {

    private static final String ORDERS_TOPIC_SR = "orders-sr";

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        KafkaProducer<String, Order> producer = configureProducer();

        Order order = buildNewOrder();

        ProducerRecord<String, Order> producerRecord =
                new ProducerRecord<>(ORDERS_TOPIC_SR, order.getId().toString(),order);
        var recordMetaData = producer.send(producerRecord).get();

        System.out.println("Message produced, record metadata: " + recordMetaData);

        producer.flush();
        producer.close();
    }

    private static KafkaProducer<String, Order> configureProducer() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        properties.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");
        properties.put(KafkaAvroSerializerConfig.VALUE_SUBJECT_NAME_STRATEGY, RecordNameStrategy.class);

        return new KafkaProducer<>(properties);
    }

    private static Order buildNewOrder() {
        return Order.newBuilder()
                .setId(UUID.randomUUID())
                .setFirstName("John")
                .setLastName("Doe")
                .setOrderedTime(Instant.now())
                .setStatus("NEW")
                .build();
    }
}
