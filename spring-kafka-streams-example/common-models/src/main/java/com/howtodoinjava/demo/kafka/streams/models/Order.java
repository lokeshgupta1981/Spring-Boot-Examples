package com.howtodoinjava.demo.kafka.streams.models;

import java.math.BigDecimal;

public record Order(Integer orderId,
                    String productId,
                    BigDecimal finalAmount) {

}
