package com.howtodoinjava.demo.kafka.streams.service.controllers;

import com.howtodoinjava.demo.kafka.streams.models.OrdersCountPerStoreDTO;
import com.howtodoinjava.demo.kafka.streams.service.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/orders")
public class OrdersController {

  @Autowired
  private OrderService orderService;

  @GetMapping("/count")
  public List<OrdersCountPerStoreDTO> ordersCount() {
    return orderService.ordersCount();
  }

}
