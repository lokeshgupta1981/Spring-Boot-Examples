package com.howtodoinjava.kafkaproducerconsumer.service;

import com.howtodoinjava.kafkaproducerconsumer.constants.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer
{
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducer.class);

    private final KafkaTemplate<Long, String> template;

    public KafkaProducer(KafkaTemplate<Long, String> template)
    {
        this.template = template;
    }

    public void sendMessage(Long id, String message)
    {
        LOGGER.info("Message Sent To The Topic {} With Key {} == {}", Constants.TOPIC_NAME, id, message);

        template.send(Constants.TOPIC_NAME, id, message);
    }
}
