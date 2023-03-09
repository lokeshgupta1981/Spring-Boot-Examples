package com.howtodoinjava.app;


import com.howtodoinjava.app.model.order.Order;
import com.howtodoinjava.app.model.user.User;
import com.howtodoinjava.app.repositories.order.OrderRepository;
import com.howtodoinjava.app.repositories.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class AppTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private OrderRepository orderRepository;

  User user;
  Order order;

  @BeforeEach
  public void setup() {
    user = new User();
    order = new Order();

    user.setEmail("johndoe@gmail.com");
    user.setFirstName("John");
    user.setLastName("DOe");

    order.setProductName("Mobile");
    order.setOrderAmount(200);
    order.setUserId(1);
  }

  @Test
  void contextLoads() {
  }


  @Test
  void saveUserDataInDB() {

    User savedUser = userRepository.save(user);
    Optional<User> userData = userRepository.findById(savedUser.getId());
    assertTrue(userData.isPresent());

  }

  @Test
  void saveOrderDataInDB() {

    Order savedOrder = orderRepository.save(order);
    Optional<Order> orderData = orderRepository.findById(savedOrder.getId());
    assertTrue(orderData.isPresent());
  }

}

