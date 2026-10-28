package com.howtodoinjava.restclient;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withResourceNotFound;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(ShippingClient.class)
class ShippingClientTest {
    @Autowired ShippingClient client;
    @Autowired MockRestServiceServer server;

    @Test
    void mapsShipmentStatus() {
        server.expect(requestTo("https://shipping.example/api/shipments/TRK-42"))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess("""
                {"trackingId":"TRK-42","status":"IN_TRANSIT"}
                """, MediaType.APPLICATION_JSON));

        assertThat(client.statusOf("TRK-42"))
            .contains(new ShipmentStatus("TRK-42", "IN_TRANSIT"));
        server.verify();
    }

    @Test
    void returnsEmptyWhenShipmentIsMissing() {
        server.expect(requestTo("https://shipping.example/api/shipments/TRK-99"))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withResourceNotFound());

        assertThat(client.statusOf("TRK-99")).isEmpty();
        server.verify();
    }
}
