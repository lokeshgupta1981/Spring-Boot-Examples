package com.howtodoinjava.demo.kafka.consumer.consumer;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.howtodoinjava.demo.kafka.consumer.model.InventoryEvent;
import com.howtodoinjava.demo.kafka.consumer.model.InventoryEventType;
import com.howtodoinjava.demo.kafka.consumer.model.Product;
import com.howtodoinjava.demo.kafka.consumer.service.InventoryEventService;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@EmbeddedKafka(topics = {"inventory-events"
        , "inventory-events.RETRY"
        , "inventory-events.DLT"
}
        , partitions = 3)
@TestPropertySource(properties = {"spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}"
        , "spring.kafka.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}"
        , "retryListener.startup=false"})
class InventoryEventsConsumerTest {

    @Value("${topics.retry}")
    private String retryTopic;

    @Value("${topics.dlt}")
    private String deadLetterTopic;

    @Autowired
    EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    KafkaTemplate<Integer, Object> kafkaTemplate;

    @Autowired
    KafkaListenerEndpointRegistry endpointRegistry;

    @SpyBean
    InventoryEventsConsumer inventoryEventsConsumerSpy;

    @SpyBean
    InventoryEventService inventoryEventServiceSpy;

    @Autowired
    ObjectMapper objectMapper;

    private Consumer<Integer, String> consumer;

    @BeforeEach
    void setUp() {

        var container = endpointRegistry.getListenerContainers()
                .stream().filter(messageListenerContainer ->
                        Objects.equals(messageListenerContainer.getGroupId(), "inventory-consumer-group-1"))
                .toList().get(0);
        ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());
//        for (MessageListenerContainer messageListenerContainer : endpointRegistry.getListenerContainers()) {
//            System.out.println("Group Id : "+ messageListenerContainer.getGroupId());
//            if(Objects.equals(messageListenerContainer.getGroupId(), "library-events-listener-group")){
//                System.out.println("Waiting for assignment");
//                ContainerTestUtils.waitForAssignment(messageListenerContainer, embeddedKafkaBroker.getPartitionsPerTopic());
//            }
//        }
    }

    @AfterEach
    void tearDown() {

    }

    @Test
    void publishNewInventoryEvent_Success() throws ExecutionException, InterruptedException, JsonProcessingException {
        //given
        Product product = Product.builder()
                .productId(456)
                .productName("Samsung S23")
                .price("75000")
                .quantity(50)
                .build();

        InventoryEvent inventoryEvent = InventoryEvent.builder()
                .inventoryId(null)
                .inventoryEventType(InventoryEventType.NEW)
                .product(product)
                .build();

        kafkaTemplate.send("inventory-events", inventoryEvent).get();

        //when
        CountDownLatch latch = new CountDownLatch(1);
        latch.await(3, TimeUnit.SECONDS);

        //then
        verify(inventoryEventsConsumerSpy, times(1)).onMessage(isA(ConsumerRecord.class));
        verify(inventoryEventServiceSpy, times(1)).processLibraryEvent(isA(ConsumerRecord.class));
    }

    @Test
    void publishModifyInventoryEvent_Null_InventoryId_DeadLetter_Topic() throws JsonProcessingException, InterruptedException, ExecutionException {
        //given
        Product product = Product.builder()
                .productId(456)
                .productName("Samsung S23")
                .price("75000")
                .quantity(50)
                .build();

        InventoryEvent inventoryEvent = InventoryEvent.builder()
                .inventoryId(null)
                .inventoryEventType(InventoryEventType.UPDATE)
                .product(product)
                .build();

        String json = objectMapper.writeValueAsString(inventoryEvent);

        kafkaTemplate.send("inventory-events", inventoryEvent).get();
        //when
        CountDownLatch latch = new CountDownLatch(1);
        latch.await(3, TimeUnit.SECONDS);


        verify(inventoryEventsConsumerSpy, times(1)).onMessage(isA(ConsumerRecord.class));
        verify(inventoryEventServiceSpy, times(1)).processLibraryEvent(isA(ConsumerRecord.class));

        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.consumerProps("group3", "true", embeddedKafkaBroker));
        consumer = new DefaultKafkaConsumerFactory<>(configs, new IntegerDeserializer(), new StringDeserializer()).createConsumer();
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, deadLetterTopic);

        //then
        ConsumerRecords<Integer, String> consumerRecords = KafkaTestUtils.getRecords(consumer);

        var deadletterList = new ArrayList<ConsumerRecord<Integer, String>>();
        consumerRecords.forEach((record) -> {
            if (record.topic().equals(deadLetterTopic)) {
                deadletterList.add(record);
            }
        });

        var finalList = deadletterList.stream()
                .filter(record -> record.value().equals(json))
                .toList();

        assert finalList.size() == 1;
    }

}