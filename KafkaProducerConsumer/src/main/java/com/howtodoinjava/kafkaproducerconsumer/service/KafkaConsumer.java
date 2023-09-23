package com.howtodoinjava.kafkaproducerconsumer.service;

import com.howtodoinjava.kafkaproducerconsumer.constants.Constants;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer
{
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);

    @KafkaListener(topics = Constants.TOPIC_NAME, groupId = Constants.GROUP_ID)
    public void receive(ConsumerRecord<Long, String> payload)
    {
        LOGGER.info("Message Received!!");
        LOGGER.info("Topic: {}", Constants.TOPIC_NAME);
        LOGGER.info("key: {}", payload.key());
        LOGGER.info("Headers: {}", payload.headers());
        LOGGER.info("Partition: {}", payload.partition());
        LOGGER.info("Value: {}", payload.value());
    }
}
