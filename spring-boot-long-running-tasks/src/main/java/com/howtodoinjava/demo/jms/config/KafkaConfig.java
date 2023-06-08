package com.howtodoinjava.demo.jms.config;

import com.howtodoinjava.demo.web.model.TaskStatus;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Configuration
public class KafkaConfig {

  @Autowired KafkaProperties kafkaProperties;

  @Bean
  public NewTopic taskTopic() {
    return TopicBuilder.name("task-topic")
        .partitions(1)
        .replicas(1)
        .build();
  }

  @Bean
  public KafkaAdmin kafkaAdmin() {
    KafkaAdmin kafkaAdmin = new KafkaAdmin(kafkaProperties.buildAdminProperties());
    kafkaAdmin.setFatalIfBrokerNotAvailable(kafkaProperties.getAdmin().isFailFast());
    return kafkaAdmin;
  }

  @Bean
  @ConditionalOnMissingBean
  public ConsumerFactory<String, TaskStatus> consumerFactory() {
    Map<String, Object> configProps = new HashMap<>();
    configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
    configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
    configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "task-group");
    configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
    configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

    return new DefaultKafkaConsumerFactory<>(configProps);
  }

  @Bean
  public KafkaConsumer<String, TaskStatus> kafkaConsumer() {
    return (KafkaConsumer<String, TaskStatus>) consumerFactory().createConsumer();
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, TaskStatus> kafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, TaskStatus> factory = new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory());
    /*factory.setMessageConverter(new StringJsonMessageConverter());*/
    return factory;
  }
}
