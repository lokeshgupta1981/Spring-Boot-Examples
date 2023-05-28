package com.howtodoinjava.demo.publisher.controller;

import com.howtodoinjava.demo.model.OrderEvents;
import com.howtodoinjava.demo.publisher.pubsub.service.RedisPubSubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    @Autowired
    private RedisPubSubService redisPubSubService;

    @PostMapping("/publish")
    public String publish(@RequestBody OrderEvents orderEvents) {
        redisPubSubService.publish(orderEvents);
        return "Success";
    }

}