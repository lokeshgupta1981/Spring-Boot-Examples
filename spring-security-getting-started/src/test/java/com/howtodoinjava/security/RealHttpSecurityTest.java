package com.howtodoinjava.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

/**
 * The same rules over real HTTP on embedded Tomcat, the way curl sees them.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RealHttpSecurityTest {

  @LocalServerPort int port;

  private final HttpClient client = HttpClient.newHttpClient();

  private HttpResponse<String> send(String method, String path, String user, String password) throws Exception {
    HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
        .method(method, method.equals("POST")
            ? HttpRequest.BodyPublishers.ofString("Road Trip")
            : HttpRequest.BodyPublishers.noBody());
    if (user != null) {
      String token = Base64.getEncoder().encodeToString((user + ":" + password).getBytes());
      builder.header("Authorization", "Basic " + token);
    }
    return client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
  }

  @Test
  void statusCodesForEachRule() throws Exception {
    assertThat(send("GET", "/public/top-songs", null, null).statusCode()).isEqualTo(200);
    assertThat(send("GET", "/playlists", null, null).statusCode()).isEqualTo(401);
    assertThat(send("GET", "/playlists", "lokesh", "password").statusCode()).isEqualTo(200);
    assertThat(send("GET", "/playlists", "lokesh", "wrong").statusCode()).isEqualTo(401);
    assertThat(send("GET", "/admin/report", "lokesh", "password").statusCode()).isEqualTo(403);
    assertThat(send("GET", "/admin/report", "admin", "admin123").statusCode()).isEqualTo(200);
  }

  @Test
  void postWithoutCsrfTokenGets403BecauseErrorPageIsPermitted() throws Exception {
    HttpResponse<String> response = send("POST", "/playlists", "lokesh", "password");
    System.out.println("POST> " + response.statusCode() + " " + response.body());
    assertThat(response.statusCode()).isEqualTo(403);
  }
}
