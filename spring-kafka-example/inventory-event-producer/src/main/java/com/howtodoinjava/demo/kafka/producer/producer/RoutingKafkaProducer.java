package com.howtodoinjava.demo.kafka.producer.producer;

import com.howtodoinjava.demo.kafka.producer.model.InventoryEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.RoutingKafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RoutingKafkaProducer {

    private RoutingKafkaTemplate routingTemplate;

    public void sendDefaultTopic(String message) {
        routingTemplate.send("default-topic", message.getBytes());
    }

    public void sendInventoryEvent(InventoryEvent inventoryEvent) {

        routingTemplate.send("inventory-events", inventoryEvent);
    }
}
