package com.howtodoinjava.demo.publisher.controller;

import com.howtodoinjava.demo.model.OrderEvent;
import com.howtodoinjava.demo.publisher.pubsub.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/publish")
    public String publish(@RequestBody OrderEvent orderEvent) {
        orderService.publish(orderEvent);
        return "Success";
    }

}