package com.howtodoinjava.demo.kafka.consumer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.howtodoinjava.demo.kafka.consumer.model.InventoryEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class InventoryEventService {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    KafkaTemplate<Integer,String> kafkaTemplate;

    public void processLibraryEvent(ConsumerRecord<Integer,String> consumerRecord) throws JsonProcessingException {
        InventoryEvent inventoryEvent = objectMapper.readValue(consumerRecord.value(), InventoryEvent.class);
        log.info("libraryEvent : {} ", inventoryEvent);

        if(inventoryEvent.getInventoryId() != null && ( inventoryEvent.getInventoryId() == 999 )){
            throw new RecoverableDataAccessException("Temporary Network Issue");
        }

        switch(inventoryEvent.getInventoryEventType()){
            case NEW:
                save(inventoryEvent);
                break;
            case UPDATE:
                validate(inventoryEvent);
                save(inventoryEvent);
                break;
            default:
                log.info("Invalid Library Event Type");
        }

    }

    private void save(InventoryEvent inventoryEvent) {
        log.info("Successfully Persisted the inventory Event {} ", inventoryEvent);
    }

    private void validate(InventoryEvent inventoryEvent) {

        if (inventoryEvent.getInventoryId() == null || inventoryEvent.getProduct().getProductId() == null) {
            throw new IllegalArgumentException("Library Event Id is missing");
        }

        log.info("Validation is successful for the library Event : {} ", inventoryEvent);
    }



}
