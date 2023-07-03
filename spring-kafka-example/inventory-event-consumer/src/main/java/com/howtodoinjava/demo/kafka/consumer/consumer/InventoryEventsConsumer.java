package com.howtodoinjava.demo.kafka.consumer.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.howtodoinjava.demo.kafka.consumer.model.InventoryEvent;
import com.howtodoinjava.demo.kafka.consumer.service.InventoryEventService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class InventoryEventsConsumer {

    @Autowired
    private InventoryEventService inventoryEventService;

    @KafkaListener(topics = {"inventory-events"}, groupId = "inventory-consumer-group-1")
    public void onMessage(ConsumerRecord<Integer, InventoryEvent> consumerRecord) throws JsonProcessingException {
        inventoryEventService.processLibraryEvent(consumerRecord);
        log.info("Consumer Record: {}", consumerRecord);
    }

    /**
     *  ----- consumer group and partition with intial offset  ----
     *
    @KafkaListener(groupId = "inventory-consumer-group-1",
            topicPartitions = @TopicPartition(topic = "inventory-events",
                    partitionOffsets = {
                            @PartitionOffset(partition = "0", initialOffset = "0"),
                            @PartitionOffset(partition = "2", initialOffset = "0")}))
     */
    public void onMessage_PartitionIntialOffset(ConsumerRecord<Integer, InventoryEvent> consumerRecord) {
        log.info("Consumer Record: {}", consumerRecord);
    }


    /**
     * ----- consumer group and partition with no intial offset  ----
     *
    @KafkaListener(topicPartitions = @TopicPartition(topic = "inventory-events", partitions = { "0", "1" }))
     */
    public void onMessage_PartitionNoOffset(ConsumerRecord<Integer, InventoryEvent> consumerRecord) {
        log.info("Consumer Record: {}", consumerRecord);
    }
}
