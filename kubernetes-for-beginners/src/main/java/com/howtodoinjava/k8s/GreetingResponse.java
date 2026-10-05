package com.howtodoinjava.k8s;

public record GreetingResponse(String message, long visits, String pod, String version) {
}
