package com.howtodoinjava.templatebuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

@SpringBootTest(properties = "delivery.base-url=https://delivery.example.test")
class DeliveryQuoteClientTest {
    @Autowired DeliveryQuoteClient client;

    @Test void baseUriAndLocalAndGlobalHeaders() {
        var server = MockRestServiceServer.bindTo(client.restTemplate()).build();
        server.expect(requestTo("https://delivery.example.test/quotes/90210"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Application", "checkout"))
                .andExpect(header("X-Platform", "storefront"))
                .andRespond(withSuccess("{\"postalCode\":\"90210\",\"days\":2}", MediaType.APPLICATION_JSON));

        assertThat(client.quote("90210")).isEqualTo(new DeliveryQuoteClient.DeliveryQuote("90210", 2));
        server.verify();
    }
}
