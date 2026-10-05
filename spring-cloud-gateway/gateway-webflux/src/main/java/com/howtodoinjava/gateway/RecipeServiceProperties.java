package com.howtodoinjava.gateway;

import java.net.URI;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** Binds recipe-service.uri from application.yml. */
@ConfigurationProperties("recipe-service")
public record RecipeServiceProperties(URI uri) {
}
