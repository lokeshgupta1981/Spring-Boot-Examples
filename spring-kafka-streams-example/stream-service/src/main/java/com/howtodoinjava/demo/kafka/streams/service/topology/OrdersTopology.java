package com.howtodoinjava.demo.kafka.streams.service.topology;

import com.howtodoinjava.demo.kafka.streams.models.Order;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrdersTopology {

  public static final String ORDERS = "orders";
  public static final String ORDERS_COUNT = "orders_count";
  public static final String ORDERS_OUTPUT = "orders-output";

  @Autowired
  public void process(StreamsBuilder streamsBuilder) {

    KStream<String, Order> orderStreams = streamsBuilder.stream(ORDERS,
            Consumed.with(Serdes.String(), new JsonSerde<>(Order.class)))
        .selectKey((key, value) -> value.productId());

    orderStreams.print(Printed.<String, Order>toSysOut().withLabel("orders"));
    ordersCount(orderStreams);
  }

  private void ordersCount(KStream<String, Order> generalOrdersStream) {

    KTable<String, Long> ordersCount = generalOrdersStream.map(
            (key, order) -> KeyValue.pair(order.productId(), order))
        .groupByKey(Grouped.with(Serdes.String(), new JsonSerde<>(Order.class)))
        .count(Named.as(OrdersTopology.ORDERS_COUNT),
            //use state store to save data
            Materialized.as(OrdersTopology.ORDERS_COUNT));

    ordersCount.toStream()
        .print(Printed.<String, Long>toSysOut().withLabel(OrdersTopology.ORDERS_COUNT));

    /**
     * Publish data to output kafka topic
     *
     ordersCount.toStream()
     .to(ORDERS_OUTPUT, Produced.with(Serdes.String(), Serdes.Long()));
     */
  }

}