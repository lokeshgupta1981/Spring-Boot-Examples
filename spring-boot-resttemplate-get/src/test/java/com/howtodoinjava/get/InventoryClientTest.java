package com.howtodoinjava.get;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InventoryClientTest {
    @LocalServerPort int port;
    @Autowired InventoryClient client;
    String base() { return "http://localhost:" + port; }

    @Test void bodyOnly() {
        var stock = client.body(base(), "BOOK-42", "west");
        assertNotNull(stock);
        assertEquals("west", stock.warehouse());
        assertEquals(4, stock.available());
    }

    @Test void fullResponse() {
        var response = client.response(base(), "BOOK-42", "main");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(12, response.getBody().available());
        assertEquals("none", response.getHeaders().getFirst("X-Request-Id"));
    }

    @Test void customRequestHeader() {
        var response = client.withRequestId(base(), "BOOK-42", "west", "trace-123");
        assertEquals("trace-123", response.getHeaders().getFirst("X-Request-Id"));
        assertEquals(4, response.getBody().available());
    }

    @Test void missingSkuRaises404() {
        var error = assertThrows(HttpClientErrorException.NotFound.class,
                () -> client.body(base(), "MISSING", "main"));
        assertEquals(HttpStatus.NOT_FOUND, error.getStatusCode());
    }
}
