package com.howtodoinjava.demo.publisher.pubsub.service;

import com.howtodoinjava.demo.model.OrderEvents;

public interface RedisPubSubService {

    Long publish(OrderEvents orderEvents);
}
