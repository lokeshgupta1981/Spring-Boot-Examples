package com.howtodoinjava.demo.kafka.producer.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.howtodoinjava.demo.kafka.producer.model.InventoryEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class InventoryEventProducer {

    @Value("${spring.kafka.topic}")
    public String topic;

    @Autowired
    private KafkaTemplate<Integer, Object> kafkaTemplate;

    public CompletableFuture<SendResult<Integer, Object>> sendInventoryEvent_Async(InventoryEvent inventoryEvent) throws JsonProcessingException {

        var key = inventoryEvent.getInventoryId();

        var completableFuture = kafkaTemplate.send(topic, key, inventoryEvent);

        return completableFuture.whenComplete(((sendResult, throwable) -> {
            if (throwable != null) {
                handleFailure(key, inventoryEvent, throwable);
            } else {
                handleSuccess(key, inventoryEvent, sendResult);
            }
        }));
    }

    public CompletableFuture<SendResult<Integer, Object>> sendInventoryEvent_ProducerRecord(InventoryEvent inventoryEvent) throws JsonProcessingException {

        var key = inventoryEvent.getInventoryId();
        var producerRecord = buildProducerRecord(key, inventoryEvent);

        var completableFuture = kafkaTemplate.send(producerRecord);

        return completableFuture.whenComplete(((sendResult, throwable) -> {
            if (throwable != null) {
                handleFailure(key, inventoryEvent, throwable);
            } else {
                handleSuccess(key, inventoryEvent, sendResult);
            }
        }));
    }

    private ProducerRecord<Integer, Object> buildProducerRecord(Integer key, Object value) {
        List<Header> recordHeader = List.of(new RecordHeader("event-source", "library-event-producer".getBytes()));
        return new ProducerRecord<>(topic, null, key, value, recordHeader);
    }

    private void handleSuccess(Integer key, Object value, SendResult<Integer, Object> sendResult) {
        log.info("Message sent successfully for the key: {} and the value: {}, partition is: {}",
                key, value, sendResult.getRecordMetadata().partition());
    }

    private void handleFailure(Integer key, Object value, Throwable throwable) {
        log.error("Error sending message and exception is {}", throwable.getMessage(), throwable);
    }
}
