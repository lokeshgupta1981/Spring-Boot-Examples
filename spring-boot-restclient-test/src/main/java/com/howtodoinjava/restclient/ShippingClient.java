package com.howtodoinjava.restclient;

import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Component
public class ShippingClient {
    private final RestClient restClient;

    public ShippingClient(RestClient.Builder builder) {
        this.restClient = builder.baseUrl("https://shipping.example/api").build();
    }

    public Optional<ShipmentStatus> statusOf(String trackingId) {
        try {
            ShipmentStatus status = restClient.get()
                .uri("/shipments/{trackingId}", trackingId)
                .retrieve().body(ShipmentStatus.class);
            return Optional.ofNullable(status);
        } catch (HttpClientErrorException.NotFound ex) {
            return Optional.empty();
        }
    }
}
