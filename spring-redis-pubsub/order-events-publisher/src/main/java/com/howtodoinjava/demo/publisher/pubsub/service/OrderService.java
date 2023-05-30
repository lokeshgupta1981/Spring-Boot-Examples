package com.howtodoinjava.demo.publisher.pubsub.service;

import com.howtodoinjava.demo.model.OrderEvent;

public interface OrderService {

    Long publish(OrderEvent orderEvent);
}
