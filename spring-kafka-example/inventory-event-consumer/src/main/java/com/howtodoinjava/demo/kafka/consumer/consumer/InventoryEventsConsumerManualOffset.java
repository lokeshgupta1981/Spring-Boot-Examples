package com.howtodoinjava.demo.kafka.consumer.consumer;

import com.howtodoinjava.demo.kafka.consumer.model.InventoryEvent;
import com.howtodoinjava.demo.kafka.consumer.service.InventoryEventService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

//@Component
@Slf4j
public class InventoryEventsConsumerManualOffset implements AcknowledgingMessageListener<Integer, InventoryEvent> {

    @Autowired
    private InventoryEventService inventoryEventService;

    @Override
    @KafkaListener(topics = "inventory-events", groupId = "inventory-consumer-group-1")
    public void onMessage(ConsumerRecord<Integer, InventoryEvent> consumerRecord, Acknowledgment acknowledgment) {
        log.info("Consumer Record: {}", consumerRecord);
        acknowledgment.acknowledge();
    }
}
