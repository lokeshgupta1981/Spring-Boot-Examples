package com.howtodoinjava.avro.example.consumer;

import com.howtodoinjava.avro.example.domain.generated.Order;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class OrdersConsumerUsingSchemaRegistry {

    private static final String ORDERS_TOPIC_SR = "orders-sr";

    public static void main(String[] args) {
        KafkaConsumer<String, Order> consumer = getCoffeeOrderKafkaConsumer();

        consumer.subscribe(Collections.singletonList(ORDERS_TOPIC_SR));
        System.out.println("Consumer Started");

        while(true) {
            ConsumerRecords<String, Order> records = consumer.poll(Duration.ofMillis(100));
            for(ConsumerRecord<String, Order> orderRecord : records) {

                Order order = orderRecord.value();
                System.out.println("Consumed message: \n key: " + orderRecord.key() + ", value: " + order.toString());

            }
        }
    }

    private static KafkaConsumer<String, Order> getCoffeeOrderKafkaConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "order.consumer.sr");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class.getName());
        props.put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");
        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);

        return new KafkaConsumer<>(props);
    }

}