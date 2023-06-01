package com.howtodoinjava.demo.redis.streams.producer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.howtodoinjava.demo.redis.streams.model.PurchaseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
public class PurchaseStreamProducer {

  @Autowired
  private RedisTemplate<String, String> redisTemplate;

  @Value("${stream.key:purchase-events}")
  private String streamKey;

  public RecordId produce(PurchaseEvent purchaseEvent) throws JsonProcessingException {
    log.info("purchase details: {}", purchaseEvent);

    ObjectRecord<String, PurchaseEvent> record = StreamRecords.newRecord()
        .ofObject(purchaseEvent)
        .withStreamKey(streamKey);

    RecordId recordId = this.redisTemplate.opsForStream()
        .add(record);

    log.info("recordId: {}", recordId);

    if (Objects.isNull(recordId)) {
      log.info("error sending event: {}", purchaseEvent);
      return null;
    }

    return recordId;
  }
}
