package com.howtodoinjava.demo.kafka.streams.service.producer;

import static com.howtodoinjava.demo.kafka.streams.service.producer.ProducerUtil.publishMessageSync;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.howtodoinjava.demo.kafka.streams.models.Order;
import com.howtodoinjava.demo.kafka.streams.service.topology.OrdersTopology;
import java.math.BigDecimal;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OrdersMockDataProducer {

  public static void main(String[] args) throws InterruptedException {
    ObjectMapper objectMapper = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    publishOrders(objectMapper, buildOrders());

  }

  private static List<Order> buildOrders() {

    var order1 = new Order(54321, "product_1234",
        new BigDecimal("27.00")
    );

    var order2 = new Order(54321, "product_1234",
        new BigDecimal("15.00")
    );

    var order3 = new Order(12345, "product_4567",
        new BigDecimal("27.00")
    );

    var order4 = new Order(12345, "product_4567",
        new BigDecimal("27.00")
    );

    return List.of(
        order1,
        order2,
        order3,
        order4
    );
  }

  private static void publishOrders(ObjectMapper objectMapper, List<Order> orders) {

    orders
        .forEach(order -> {
          try {
            var ordersJSON = objectMapper.writeValueAsString(order);
            var recordMetaData = publishMessageSync(OrdersTopology.ORDERS, order.orderId() + "",
                ordersJSON);
            log.info("Published the order message : {} ", recordMetaData);
          } catch (JsonProcessingException e) {
            log.error("JsonProcessingException : {} ", e.getMessage(), e);
            throw new RuntimeException(e);
          } catch (Exception e) {
            log.error("Exception : {} ", e.getMessage(), e);
            throw new RuntimeException(e);
          }
        });
  }

}