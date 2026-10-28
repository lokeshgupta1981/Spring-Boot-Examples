package com.howtodoinjava.resttemplate;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AppointmentClientTest {
    @LocalServerPort int port;
    @Autowired AppointmentClient client;
    String base() { return "http://localhost:" + port; }

    @Test void createReadReplaceDeleteLifecycle() {
        var created = client.create(base(), "Dental checkup");
        assertEquals(HttpStatus.CREATED, created.getStatusCode());
        long id = created.getBody().id();
        assertEquals("/appointments/" + id, created.getHeaders().getLocation().toString());
        assertEquals("Dental checkup", client.find(base(), id).getBody().title());

        client.replace(base(), id, "Annual checkup");
        assertEquals("Annual checkup", client.body(base(), id).title());

        client.remove(base(), id);
        assertThrows(HttpClientErrorException.NotFound.class, () -> client.find(base(), id));
    }

    @Test void exchangeSendsRequestIdAndReadsResponseHeader() {
        long id = client.create(base(), "Vision test").getBody().id();
        var response = client.findWithRequestId(base(), id, "trace-456");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("trace-456", response.getHeaders().getFirst("X-Request-Id"));
        assertEquals("Vision test", response.getBody().title());
    }

    @Test void invalidCreateIsBadRequest() {
        var error = assertThrows(HttpClientErrorException.BadRequest.class,
                () -> client.create(base(), " "));
        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
    }

    @Test void missingGetIsNotFound() {
        var error = assertThrows(HttpClientErrorException.NotFound.class,
                () -> client.find(base(), 999_999));
        assertEquals(HttpStatus.NOT_FOUND, error.getStatusCode());
    }
}
