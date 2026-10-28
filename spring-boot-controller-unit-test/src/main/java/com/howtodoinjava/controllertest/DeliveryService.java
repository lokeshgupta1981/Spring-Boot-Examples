package com.howtodoinjava.controllertest;

import java.util.Optional;

public interface DeliveryService {
  Optional<DeliveryStatus> find(String trackingId);
  DeliveryStatus create(String address);
}
