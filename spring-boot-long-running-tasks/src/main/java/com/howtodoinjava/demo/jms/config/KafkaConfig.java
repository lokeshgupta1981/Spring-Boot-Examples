package com.howtodoinjava.demo.jms.config;

import com.howtodoinjava.demo.web.model.TaskStatus;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
public class KafkaConfig {

  /*@Autowired
  private KafkaProperties kafkaProperties;

  @Value("${spring.kafka.bootstrap-servers:PLAINTEXT_HOST://localhost:19092}")
  private String bootstrapServers;*/

  /*@Bean
  public KafkaTemplate<String, TaskStatus> kafkaTemplate(ProducerFactory<String, TaskStatus> pf) {
    KafkaTemplate kafkaTemplate =  new KafkaTemplate<>(pf,
        Map.of(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class));

    kafkaTemplate.setConsumerFactory(consumerFactory());
    return kafkaTemplate;
  }*/

  @Bean
  public NewTopic taskTopic() {
    return TopicBuilder.name("task-topic")
        .partitions(1)
        .replicas(1)
        .build();
  }

  /*@Bean
  @ConditionalOnMissingBean
  public KafkaAdmin kafkaAdmin(@Autowired KafkaProperties properties) {
    KafkaAdmin kafkaAdmin = new KafkaAdmin(properties.buildAdminProperties());
    kafkaAdmin.setFatalIfBrokerNotAvailable(properties.getAdmin().isFailFast());
    return kafkaAdmin;
  }*/


  /*@Bean
  public ConsumerFactory<String, TaskStatus> consumerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(),
        new JsonDeserializer<>(TaskStatus.class));
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, TaskStatus> kafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, TaskStatus> factory = new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory());
    factory.setMessageConverter(new StringJsonMessageConverter());
    return factory;
  }*/
}
