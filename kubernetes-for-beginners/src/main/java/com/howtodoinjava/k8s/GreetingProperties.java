package com.howtodoinjava.k8s;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * greeting.message comes from the ConfigMap (env GREETING_MESSAGE),
 * greeting.api-key comes from the Secret (env GREETING_API_KEY).
 */
@ConfigurationProperties(prefix = "greeting")
public record GreetingProperties(String message, String apiKey) {
}
