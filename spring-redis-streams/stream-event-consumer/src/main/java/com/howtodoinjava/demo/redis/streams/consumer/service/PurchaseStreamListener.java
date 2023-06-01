package com.howtodoinjava.demo.redis.streams.consumer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.howtodoinjava.demo.redis.streams.model.PurchaseEvent;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Service;

import java.net.InetAddress;

@Slf4j
@Service
public class PurchaseStreamListener implements StreamListener<String, ObjectRecord<String, PurchaseEvent>> {

    @Value("${stream.key:purchase-events}")
    private String streamKey;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    @SneakyThrows
    public void onMessage(ObjectRecord<String, PurchaseEvent> record) {
        PurchaseEvent purchaseEvent = record.getValue();
        log.info(InetAddress.getLocalHost().getHostName() + " - consumed :" + purchaseEvent);
        redisTemplate.opsForValue().set(purchaseEvent.getPurchaseId(),
                objectMapper.writeValueAsString(purchaseEvent));
        redisTemplate.opsForStream().acknowledge(streamKey, record);
    }
}