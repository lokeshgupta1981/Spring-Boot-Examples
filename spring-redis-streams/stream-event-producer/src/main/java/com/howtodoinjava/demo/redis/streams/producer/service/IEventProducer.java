package com.howtodoinjava.demo.redis.streams.producer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.howtodoinjava.demo.redis.streams.model.PurchaseEvent;
import org.springframework.data.redis.connection.stream.RecordId;

public interface IEventProducer {

    public RecordId produce(PurchaseEvent purchaseEvent) throws JsonProcessingException;
}
