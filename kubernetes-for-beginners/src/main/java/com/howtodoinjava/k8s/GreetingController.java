package com.howtodoinjava.k8s;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

import org.springframework.boot.info.BuildProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

  private final GreetingProperties properties;
  private final VisitRepository visits;
  private final String version;
  private final String pod;

  public GreetingController(GreetingProperties properties, VisitRepository visits, BuildProperties build)
      throws UnknownHostException {
    this.properties = properties;
    this.visits = visits;
    this.version = build.getVersion();                     // <version> from pom.xml
    this.pod = InetAddress.getLocalHost().getHostName();   // the pod name inside Kubernetes
  }

  @GetMapping("/api/greeting")
  public ResponseEntity<?> greeting(@RequestHeader(name = "X-API-Key", required = false) String apiKey) {
    if (apiKey == null || !apiKey.equals(properties.apiKey())) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "missing or wrong X-API-Key"));
    }
    long total = visits.recordVisit(pod);
    return ResponseEntity.ok(new GreetingResponse(properties.message(), total, pod, version));
  }
}
