package com.howtodoinjava.resttemplate;

import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AppointmentClient {
    private final RestTemplate restTemplate;

    public AppointmentClient(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    public ResponseEntity<AppointmentController.Appointment> create(String baseUrl, String title) {
        return restTemplate.postForEntity(baseUrl + "/appointments",
                new AppointmentController.CreateAppointment(title),
                AppointmentController.Appointment.class);
    }

    public ResponseEntity<AppointmentController.Appointment> find(String baseUrl, long id) {
        return restTemplate.getForEntity(baseUrl + "/appointments/{id}",
                AppointmentController.Appointment.class, id);
    }

    public AppointmentController.Appointment body(String baseUrl, long id) {
        return restTemplate.getForObject(baseUrl + "/appointments/{id}",
                AppointmentController.Appointment.class, id);
    }

    public void replace(String baseUrl, long id, String title) {
        restTemplate.put(baseUrl + "/appointments/{id}",
                new AppointmentController.CreateAppointment(title), id);
    }

    public void remove(String baseUrl, long id) {
        restTemplate.delete(baseUrl + "/appointments/{id}", id);
    }

    public ResponseEntity<AppointmentController.Appointment> findWithRequestId(
            String baseUrl, long id, String requestId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Request-Id", requestId);
        return restTemplate.exchange(baseUrl + "/appointments/{id}", HttpMethod.GET,
                new HttpEntity<>(headers), AppointmentController.Appointment.class, id);
    }
}
