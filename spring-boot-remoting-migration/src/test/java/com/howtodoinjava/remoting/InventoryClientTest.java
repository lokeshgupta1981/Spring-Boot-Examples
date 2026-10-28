package com.howtodoinjava.remoting;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InventoryClientTest {
    @Value("${local.server.port}")
    int port;

    private InventoryClient client() {
        RestClient restClient = RestClient.builder()
                .baseUrl("http://localhost:" + port).build();
        return HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build().createClient(InventoryClient.class);
    }

    @Test
    void callsInventoryAcrossRealHttp() {
        assertEquals(new InventoryController.Stock("BOOK-1", 7),
                client().stock("BOOK-1"));
    }

    @Test
    void reportsMissingSkuAsHttp404() {
        assertThrows(HttpClientErrorException.NotFound.class,
                () -> client().stock("MISSING"));
    }
}
