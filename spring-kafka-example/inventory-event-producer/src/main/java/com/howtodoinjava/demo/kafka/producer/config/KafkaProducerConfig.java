package com.howtodoinjava.demo.kafka.producer.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.core.RoutingKafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.producer.bootstrap-servers}")
    private List<String> bootstrapAddress;

    @Bean
    public ProducerFactory<?, ?> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(
          ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProps.put(
          ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
        configProps.put(
          ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<?, ?> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    //@Bean
    public RoutingKafkaTemplate routingTemplate(GenericApplicationContext context,
                                                ProducerFactory<Object, Object> pf) {

        Map<String, Object> configs = new HashMap<>(pf.getConfigurationProperties());
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);
        DefaultKafkaProducerFactory<Object, Object> bytesPF = new DefaultKafkaProducerFactory<>(configs);
        context.registerBean(DefaultKafkaProducerFactory.class, "bytesPF", bytesPF);

        Map<Pattern, ProducerFactory<Object, Object>> map = new LinkedHashMap<>();
        map.put(Pattern.compile("default-topic"), bytesPF);
        map.put(Pattern.compile("inventory-events"), pf); // Default PF with StringSerializer
        return new RoutingKafkaTemplate(map);
    }

}