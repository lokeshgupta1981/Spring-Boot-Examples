package com.howtodoinjava.kafka.queues;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.kafka.autoconfigure.KafkaConnectionDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ShareKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultShareConsumerFactory;
import org.springframework.kafka.core.ShareConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties.ShareAckMode;

@Configuration
public class QueueConfig {

  public static final String TOPIC = "thumbnails";
  public static final String SHARE_GROUP = "thumbnail-workers";

  // One partition on purpose: a consumer group could use only one consumer here
  @Bean
  NewTopic thumbnailsTopic() {
    return TopicBuilder.name(TOPIC).partitions(1).replicas(1).build();
  }

  // Spring Boot 4.1 has no auto-configuration for share consumers, so we declare the factory
  @Bean
  ShareConsumerFactory<String, String> shareConsumerFactory(KafkaConnectionDetails connection) {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, connection.getConsumer().getBootstrapServers());
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.SHARE_ACQUIRE_MODE_CONFIG, "record_limit");  // never more than max.poll.records
    props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1);                 // one photo per poll
    return new DefaultShareConsumerFactory<>(props);
  }

  @Bean
  ShareKafkaListenerContainerFactory<String, String> shareListenerFactory(
      ShareConsumerFactory<String, String> shareConsumerFactory) {

    var factory = new ShareKafkaListenerContainerFactory<>(shareConsumerFactory);
    factory.getContainerProperties().setShareAckMode(ShareAckMode.MANUAL);  // we call acknowledge/release/reject
    factory.setConcurrency(2);                                              // two consumers in the share group
    return factory;
  }
}
