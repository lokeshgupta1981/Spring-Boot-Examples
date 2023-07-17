package com.howtodoinjava.demo.kafka.streams.service.config;

import com.howtodoinjava.demo.kafka.streams.service.topology.OrdersTopology;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.config.TopicBuilder;

@Slf4j
@Configuration
@EnableKafkaStreams
public class CustomStreamsConfig {

  @Bean
  public NewTopic topicBuilder() {
    return TopicBuilder.name(OrdersTopology.ORDERS)
        .partitions(2)
        .replicas(1)
        .build();
  }

}
