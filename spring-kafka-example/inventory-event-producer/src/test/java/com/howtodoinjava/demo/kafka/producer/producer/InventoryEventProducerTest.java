package com.howtodoinjava.demo.kafka.producer.producer;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.howtodoinjava.demo.kafka.producer.model.InventoryEvent;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;
import com.howtodoinjava.demo.kafka.producer.util.TestUtil;


import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryEventProducerTest {

    @Mock
    KafkaTemplate<Integer, String> kafkaTemplate;

    @Spy
    ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    InventoryEventProducer eventProducer;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(eventProducer, "topic", "inventory-events");
    }

    @Test
    void sendInventoryEvent_ProducerRecord_success() throws JsonProcessingException, ExecutionException, InterruptedException {
        //given
        InventoryEvent inventoryEvent = TestUtil.inventoryEventRecord();
        String record = objectMapper.writeValueAsString(inventoryEvent);


        ProducerRecord<Integer, String> producerRecord = new ProducerRecord<>("inventory-events", inventoryEvent.getInventoryId(), record);
        RecordMetadata recordMetadata = new RecordMetadata(new TopicPartition("inventory-events", 1),
                1, 1, System.currentTimeMillis(), 1, 2);
        SendResult<Integer, String> sendResult = new SendResult<Integer, String>(producerRecord, recordMetadata);


        var future = CompletableFuture.supplyAsync(() -> sendResult);
        when(kafkaTemplate.send(isA(ProducerRecord.class))).thenReturn(future);
        //when

        var completableFuture = eventProducer.sendInventoryEvent_ProducerRecord(inventoryEvent);

        //then
        SendResult<Integer, Object> sendResult1 = completableFuture.get();
        assert sendResult1.getRecordMetadata().partition() == 1;

    }

}