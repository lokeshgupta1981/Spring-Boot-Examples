package com.howtodoinjava.demo.redis.streams.producer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.howtodoinjava.demo.redis.streams.model.PurchaseEvent;
import com.howtodoinjava.demo.redis.streams.producer.service.PurchaseStreamProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    @Autowired
    private PurchaseStreamProducer eventProducer;

    @PostMapping("/produce")
    public RecordId produceEvent(@RequestBody PurchaseEvent purchaseEvent) throws JsonProcessingException {
       return eventProducer.produce(purchaseEvent);
    }
}
