package com.howtodoinjava.demo.kafka.producer.util;

import com.howtodoinjava.demo.kafka.producer.model.InventoryEvent;
import com.howtodoinjava.demo.kafka.producer.model.InventoryEventType;
import com.howtodoinjava.demo.kafka.producer.model.Product;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestUtil {

    public static Product productRecord(){

        return Product.builder()
                .productId(123)
                .quantity(50)
                .productName("IPhone 14")
                .price("750000")
                .build();
    }

    public static Product productRecordWithInvalidValues(){

        return Product.builder()
                .productId(null)
                .quantity(50)
                .productName("IPhone 14")
                .price("750000")
                .build();
    }

    public static InventoryEvent inventoryEventRecord(){

        return new InventoryEvent(null,
                        InventoryEventType.NEW,
                        productRecord());
    }

    public static InventoryEvent newInventoryEventRecordWithInventoryId(){

        return new InventoryEvent(123,
                        InventoryEventType.NEW,
                        productRecord());
    }

    public static InventoryEvent inventoryEventRecordUpdate(){

        return new InventoryEvent(123,
                        InventoryEventType.UPDATE,
                        productRecord());
    }

    public static InventoryEvent inventoryEventRecordUpdateWithNullInventoryId(){

        return
                new InventoryEvent(null,
                        InventoryEventType.UPDATE,
                        productRecord());
    }

    public static InventoryEvent inventoryEventRecordWithInvalidProduct(){

        return
                new InventoryEvent(null,
                        InventoryEventType.NEW,
                        productRecordWithInvalidValues());
    }

    public static InventoryEvent parseLibraryEventRecord(ObjectMapper objectMapper , String json){

        try {
            return  objectMapper.readValue(json, InventoryEvent.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }
}