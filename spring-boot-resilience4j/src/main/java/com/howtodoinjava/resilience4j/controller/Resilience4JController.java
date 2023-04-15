package com.howtodoinjava.resilience4j.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;

@RestController
public class Resilience4JController {

	@GetMapping(value = "/student/{id}")
	@RateLimiter(name = "studentRateLimiterApi", fallbackMethod = "rateLimitingFallback")
	public ResponseEntity getStudent(@PathVariable int id) {
		System.out.println("Returning student details for: " + id);
		return ResponseEntity.ok("Student" + id);
	}
	
	@GetMapping(value = "/course/{id}")
	@Bulkhead(name = "courseBulkheadApi", fallbackMethod = "bulkheadFallback")
	public ResponseEntity getCourse(@PathVariable int id) {
		System.out.println("In course details for: " + id);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Returning course details for: " + id);
		return ResponseEntity.ok("Course" + id);
	}
	
	public ResponseEntity bulkheadFallback(int id, io.github.resilience4j.bulkhead.BulkheadFullException ex) {
		System.out.println("Bulkhead applied no further calls are accepted");

		return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
				.body("Too many request - No further calls are accepted");
	}
	

	public ResponseEntity rateLimitingFallback(int id, io.github.resilience4j.ratelimiter.RequestNotPermitted ex) {
		System.out.println("Rate limit applied no further calls are accepted");

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("Retry-After", "60s"); // retry after one second

		return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).headers(responseHeaders) // send retry header
				.body("Too many request - No further calls are accepted");
	}
	
}
