package com.howtodoinjava.library.gateway;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.stripPrefix;
import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.web.servlet.function.RequestPredicates.path;

import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

@Configuration
public class RouteConfig {

  private static final Logger log = LoggerFactory.getLogger(RouteConfig.class);

  @Bean
  RouterFunction<ServerResponse> libraryRoutes(LibraryServices services) {
    return route("books")
        .route(path("/api/books/**"), http())
        .before(logRoute("books"))
        .before(uri(services.bookService()))
        .before(stripPrefix(1))                          // /api/books/1 -> /books/1
        .build()
        .and(route("loans")
            .route(path("/api/loans/**"), http())
            .before(logRoute("loans"))
            .before(uri(services.loanService()))
            .before(stripPrefix(1))                      // /api/loans -> /loans
            .build());
  }

  private static Function<ServerRequest, ServerRequest> logRoute(String routeId) {
    return request -> {
      log.info("Route {}: {} {}", routeId, request.method(), request.path());
      return request;
    };
  }
}
