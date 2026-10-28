package com.howtodoinjava.controllertest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class DeliveryControllerDirectTest {
  @Mock DeliveryService service;
  @InjectMocks DeliveryController controller;

  @Test
  void callsServiceAndReturnsItsStatus() {
    DeliveryStatus status = new DeliveryStatus("D-17", "OUT_FOR_DELIVERY");
    when(service.find("D-17")).thenReturn(Optional.of(status));

    var response = controller.find("D-17");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo(status);
    verify(service).find("D-17");
  }

  @Test
  void returnsNotFoundForMissingDelivery() {
    when(service.find("missing")).thenReturn(Optional.empty());
    assertThat(controller.find("missing").getStatusCode())
        .isEqualTo(HttpStatus.NOT_FOUND);
  }
}
