package com.howtodoinjava.gatewaymvc;

import static org.springframework.cloud.gateway.server.mvc.filter.AfterFilterFunctions.addResponseHeader;
import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.addRequestHeader;
import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.stripPrefix;
import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

@Configuration
public class MvcRouteConfig {

  @Bean
  public RouterFunction<ServerResponse> recipeRoutes(RecipeServiceProperties recipeService) {
    return route("recipes-mvc")
        .GET("/api/recipes/**", http())
        .before(uri(recipeService.uri()))
        .before(stripPrefix(1))                                  // /api/recipes/x -> /recipes/x
        .before(addRequestHeader("X-Request-Source", "recipe-gateway-mvc"))
        .after(addResponseHeader("X-Served-By", "recipe-gateway-mvc"))
        .build();
  }
}
