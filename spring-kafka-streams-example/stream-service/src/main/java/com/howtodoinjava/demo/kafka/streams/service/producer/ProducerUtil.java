package com.howtodoinjava.demo.kafka.streams.service.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Slf4j
public class ProducerUtil {

  static KafkaProducer<String, String> producer = new KafkaProducer<String, String>(
      producerProps());

  public static Map<String, Object> producerProps() {

    Map<String, Object> propsMap = new HashMap<>();
    propsMap.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    propsMap.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    propsMap.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    return propsMap;

  }


  public static RecordMetadata publishMessageSync(String topicName, String key, String message) {

    ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topicName, key, message);
    RecordMetadata recordMetadata = null;

    try {
      log.info("producerRecord : " + producerRecord);
      recordMetadata = producer.send(producerRecord).get();
    } catch (InterruptedException e) {
      log.error("InterruptedException in  publishMessageSync : {}  ", e.getMessage(), e);
    } catch (ExecutionException e) {
      log.error("ExecutionException in  publishMessageSync : {}  ", e.getMessage(), e);
    } catch (Exception e) {
      log.error("Exception in  publishMessageSync : {}  ", e.getMessage(), e);
    }
    return recordMetadata;
  }

}