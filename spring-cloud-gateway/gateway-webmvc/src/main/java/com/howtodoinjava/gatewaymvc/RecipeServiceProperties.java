package com.howtodoinjava.gatewaymvc;

import java.net.URI;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("recipe-service")
public record RecipeServiceProperties(URI uri) {
}
