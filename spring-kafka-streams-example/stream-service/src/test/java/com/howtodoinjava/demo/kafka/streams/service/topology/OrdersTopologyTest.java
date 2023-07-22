package com.howtodoinjava.demo.kafka.streams.service.topology;

import static com.howtodoinjava.demo.kafka.streams.service.topology.OrdersTopology.ORDERS;
import static com.howtodoinjava.demo.kafka.streams.service.topology.OrdersTopology.ORDERS_COUNT;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.howtodoinjava.demo.kafka.streams.models.Order;
import java.math.BigDecimal;
import java.util.List;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.support.serializer.JsonSerde;

class OrdersTopologyTest {

  TopologyTestDriver topologyTestDriver = null;
  TestInputTopic<String, Order> ordersInputTopic = null;
  static String INPUT_TOPIC = ORDERS;
  StreamsBuilder streamsBuilder;
  OrdersTopology ordersTopology = new OrdersTopology();

  @BeforeEach
  void setUp() {
    streamsBuilder = new StreamsBuilder();
    ordersTopology.process(streamsBuilder);
    topologyTestDriver = new TopologyTestDriver(streamsBuilder.build());

    ordersInputTopic =
        topologyTestDriver.
            createInputTopic(
                INPUT_TOPIC, Serdes.String().serializer(),
                new JsonSerde<Order>(Order.class).serializer());

  }

  @AfterEach
  void tearDown() {
    topologyTestDriver.close();
  }

  @Test
  void ordersCount() {

    ordersInputTopic.pipeKeyValueList(orders());

    ReadOnlyKeyValueStore<String, Long> OrdersCountStore = topologyTestDriver.getKeyValueStore(
        ORDERS_COUNT);

    var product1234OrdersCount = OrdersCountStore.get("product_1234");
    assertEquals(2, product1234OrdersCount);

    var product4567OrdersCount = OrdersCountStore.get("product_4567");
    assertEquals(2, product4567OrdersCount);

  }

  static List<KeyValue<String, Order>> orders() {

    var order1 = new Order(54321, "product_1234", new BigDecimal("27.00"));
    var order2 = new Order(54321, "product_1234", new BigDecimal("15.00"));
    var order3 = new Order(12345, "product_4567", new BigDecimal("27.00"));
    var order4 = new Order(12345, "product_4567", new BigDecimal("27.00"));

    var keyValue1 = KeyValue.pair(order1.orderId().toString(), order1);
    var keyValue2 = KeyValue.pair(order2.orderId().toString(), order2);
    var keyValue3 = KeyValue.pair(order3.orderId().toString(), order3);
    var keyValue4 = KeyValue.pair(order4.orderId().toString(), order4);

    return List.of(keyValue1, keyValue2, keyValue3, keyValue4);
  }

}