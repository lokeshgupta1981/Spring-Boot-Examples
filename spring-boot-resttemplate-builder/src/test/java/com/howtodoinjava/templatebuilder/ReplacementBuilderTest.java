package com.howtodoinjava.templatebuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;

@SpringBootTest(properties = "delivery.base-url=https://delivery.example.test")
@ActiveProfiles("replace-builder")
class ReplacementBuilderTest {
    @Autowired DeliveryQuoteClient client;

    @Test void replacementBuilderRetainsBootCustomizer() {
        var server = MockRestServiceServer.bindTo(client.restTemplate()).build();
        server.expect(requestTo("https://delivery.example.test/quotes/10001"))
                .andExpect(header("X-Platform", "storefront"))
                .andRespond(withSuccess("{\"postalCode\":\"10001\",\"days\":3}", MediaType.APPLICATION_JSON));

        assertThat(client.quote("10001").days()).isEqualTo(3);
        server.verify();
    }
}
