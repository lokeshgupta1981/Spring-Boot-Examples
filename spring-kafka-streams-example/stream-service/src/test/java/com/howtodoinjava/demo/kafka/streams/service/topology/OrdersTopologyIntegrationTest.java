package com.howtodoinjava.demo.kafka.streams.service.topology;

import static com.howtodoinjava.demo.kafka.streams.service.topology.OrdersTopology.ORDERS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.howtodoinjava.demo.kafka.streams.models.Order;
import com.howtodoinjava.demo.kafka.streams.service.service.OrderService;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import org.apache.kafka.streams.KeyValue;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@EmbeddedKafka(topics = {ORDERS})
@TestPropertySource(properties = {
    "spring.kafka.streams.bootstrap-servers=${spring.embedded.kafka.brokers}",
    "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}"
    , "spring.kafka.streams.auto-startup=false"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class OrdersTopologyIntegrationTest {

  @Autowired
  KafkaTemplate<String, String> kafkaTemplate;

  @Autowired
  StreamsBuilderFactoryBean streamsBuilderFactoryBean;
  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  OrderService orderService;

  @BeforeEach
  public void setUp() {
    streamsBuilderFactoryBean.start();
  }

  @AfterEach
  public void destroy() {
    streamsBuilderFactoryBean.getKafkaStreams().close();
    streamsBuilderFactoryBean.getKafkaStreams().cleanUp();
  }

  @Test
  void ordersCount() {
    publishOrders();

    Awaitility.await().atMost(10, SECONDS)
        .pollDelay(Duration.ofSeconds(1))
        .ignoreExceptions()
        .until(() -> orderService.ordersCount().size(), equalTo(1));

    var ordersCount = orderService.ordersCount();
    assertEquals(1, ordersCount.get(0).count());
  }

  private void publishOrders() {
    orders()
        .forEach(order -> {
          String orderJSON = null;
          try {
            orderJSON = objectMapper.writeValueAsString(order.value);
          } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
          }
          kafkaTemplate.send(ORDERS, order.key, orderJSON);
        });
  }


  static List<KeyValue<String, Order>> orders() {

    var order1 = new Order(12345, "product_1234", new BigDecimal("15.00"));
    var keyValue1 = KeyValue.pair(order1.orderId().toString(), order1);
    return List.of(keyValue1);
  }
}