package com.howtodoinjava.logging;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

/**
 * Changes a log level at runtime through POST /actuator/loggers/{name}.
 */
@ExtendWith({OutputCaptureExtension.class, RestoreSystemProperties.class})
class ActuatorLoggersTest {

  @Test
  void changeLevelAtRuntime(CapturedOutput output) {
    try (var context = AppRunner.start(WebApplicationType.SERVLET, "server.port=0")) {
      String port = context.getEnvironment().getProperty("local.server.port");
      RestClient client = RestClient.create("http://localhost:" + port);

      String before = client.get().uri("/actuator/loggers/com.howtodoinjava.logging.playlist.PlaylistService")
          .retrieve().body(String.class);
      assertThat(before).isEqualTo("{\"effectiveLevel\":\"DEBUG\"}");

      String group = client.get().uri("/actuator/loggers/music").retrieve().body(String.class);
      assertThat(group).isEqualTo(
          "{\"members\":[\"com.howtodoinjava.logging.playlist\",\"com.howtodoinjava.logging.player\"]}");

      var status = client.post().uri("/actuator/loggers/com.howtodoinjava.logging.playlist")
          .contentType(MediaType.APPLICATION_JSON).body("{\"configuredLevel\":\"TRACE\"}")
          .retrieve().toBodilessEntity().getStatusCode();
      assertThat(status.value()).isEqualTo(204);

      client.post().uri("/playlists/road-trip/songs?title=Help").retrieve().body(String.class);
      assertThat(output.getOut()).containsPattern("TRACE .*PlaylistService\\s+: Checking song 'Help'");

      // Empty body resets the logger: the level is inherited again
      client.post().uri("/actuator/loggers/com.howtodoinjava.logging.playlist")
          .contentType(MediaType.APPLICATION_JSON).body("{}").retrieve().toBodilessEntity();
      String after = client.get().uri("/actuator/loggers/com.howtodoinjava.logging.playlist")
          .retrieve().body(String.class);
      assertThat(after).isEqualTo("{\"effectiveLevel\":\"DEBUG\"}");

      // A group name works too
      client.post().uri("/actuator/loggers/music")
          .contentType(MediaType.APPLICATION_JSON).body("{\"configuredLevel\":\"WARN\"}")
          .retrieve().toBodilessEntity();
      String player = client.get().uri("/actuator/loggers/com.howtodoinjava.logging.player")
          .retrieve().body(String.class);
      assertThat(player).isEqualTo("{\"configuredLevel\":\"WARN\",\"effectiveLevel\":\"WARN\"}");
    }
  }
}
