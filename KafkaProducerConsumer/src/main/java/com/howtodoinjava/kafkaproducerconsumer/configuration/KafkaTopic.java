package com.howtodoinjava.kafkaproducerconsumer.configuration;

import com.howtodoinjava.kafkaproducerconsumer.constants.Constants;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopic
{
    @Bean
    public NewTopic topic()
    {
        return TopicBuilder.name(Constants.TOPIC_NAME).partitions(3).build();
    }
}
