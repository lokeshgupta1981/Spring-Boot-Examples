package com.howtodoinjava.demo.kafka.streams.models;

public record OrdersCountPerStoreDTO(String locationId,
                                     Long count) {

}
