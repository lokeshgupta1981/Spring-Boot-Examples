package com.howtodoinjava.logging;

import static org.assertj.core.api.Assertions.assertThat;

import com.howtodoinjava.logging.player.PlayerService;
import com.howtodoinjava.logging.playlist.PlaylistService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Asserts the log lines shown in the article for each logging.* property.
 */
@ExtendWith({OutputCaptureExtension.class, RestoreSystemProperties.class})
class LoggingPropertiesTest {

  /** Adds a song twice and plays an empty and a full playlist: one line per level. */
  private static void exercise(ConfigurableApplicationContext context) {
    PlaylistService playlists = context.getBean(PlaylistService.class);
    PlayerService player = context.getBean(PlayerService.class);
    playlists.addSong("road-trip", "Yesterday");
    playlists.addSong("road-trip", "Yesterday");
    player.play("gym");
    player.play("road-trip");
  }

  @Test
  void defaultLevelIsInfo(CapturedOutput output) {
    try (var context = AppRunner.start("logging.level.root=info", "logging.level.com.howtodoinjava=info")) {
      exercise(context);
    }
    assertThat(output.getOut())
        .containsPattern("INFO \\d+ --- \\[playlist\\] \\[\\s+main\\] c\\.h\\.logging\\.playlist\\.PlaylistService\\s+: Playlist 'road-trip' now has 1 songs")
        .containsPattern(" WARN .*'Yesterday' is already in playlist 'road-trip'")
        .containsPattern("ERROR .*c\\.h\\.logging\\.player\\.PlayerService\\s+: Cannot play 'gym': the playlist is empty")
        .doesNotContain("Adding 'Yesterday'")
        .doesNotContain("Checking song");
  }

  @Test
  void packageLevelFromApplicationProperties(CapturedOutput output) {
    // application.properties: root=warn, com.howtodoinjava=debug
    try (var context = AppRunner.start()) {
      exercise(context);
    }
    assertThat(output.getOut())
        .containsPattern("DEBUG .*c\\.h\\.logging\\.playlist\\.PlaylistService\\s+: Adding 'Yesterday' to playlist 'road-trip'")
        .containsPattern("DEBUG .*c\\.h\\.logging\\.player\\.PlayerService\\s+: Loading playlist 'gym'")
        .doesNotContain("Checking song");
  }

  @Test
  void logGroupSetsLevelForBothPackages(CapturedOutput output) {
    try (var context = AppRunner.start("logging.level.music=trace")) {
      exercise(context);
    }
    assertThat(output.getOut())
        .containsPattern("TRACE .*c\\.h\\.logging\\.playlist\\.PlaylistService\\s+: Checking song 'Yesterday'");
  }

  @Test
  void customConsolePattern(CapturedOutput output) {
    try (var context = AppRunner.start("logging.pattern.console=%d{HH:mm:ss} %-5level %logger{20} - %msg%n")) {
      exercise(context);
    }
    assertThat(output.getOut())
        .containsPattern("\\d\\d:\\d\\d:\\d\\d DEBUG c\\.h\\.l\\.p\\.PlaylistService - Adding 'Yesterday' to playlist 'road-trip'")
        .containsPattern("\\d\\d:\\d\\d:\\d\\d ERROR c\\.h\\.l\\.p\\.PlayerService - Cannot play 'gym': the playlist is empty");
  }

  @Test
  void yamlConfigurationGivesSameResult(CapturedOutput output) {
    try (var context = AppRunner.start("spring.config.location=classpath:yaml/application.yml",
        "logging.level.music=trace")) {
      exercise(context);
    }
    assertThat(output.getOut())
        .containsPattern("\\d\\d:\\d\\d:\\d\\d TRACE c\\.h\\.l\\.p\\.PlaylistService - Checking song 'Yesterday'")
        .containsPattern("\\d\\d:\\d\\d:\\d\\d INFO  c\\.h\\.l\\.p\\.PlayerService - Playing 'Yesterday' from 'road-trip'");
  }

  @Test
  void structuredEcsOnConsole(CapturedOutput output) {
    try (var context = AppRunner.start("logging.structured.format.console=ecs")) {
      exercise(context);
    }
    assertThat(output.getOut())
        .contains("\"log\":{\"level\":\"INFO\",\"logger\":\"com.howtodoinjava.logging.playlist.PlaylistService\"}")
        .contains("\"service\":{\"name\":\"playlist\"")
        .contains("\"message\":\"Playlist 'road-trip' now has 1 songs\"")
        .contains("\"ecs\":{\"version\":\"8.11\"}");
  }

  @Test
  void structuredLogstashOnConsole(CapturedOutput output) {
    try (var context = AppRunner.start("logging.structured.format.console=logstash")) {
      exercise(context);
    }
    assertThat(output.getOut())
        .contains("\"message\":\"Cannot play 'gym': the playlist is empty\",\"logger_name\":\"com.howtodoinjava.logging.player.PlayerService\"")
        .contains("\"level\":\"ERROR\",\"level_value\":40000");
  }

  @Test
  void prodProfileLogsOnlyWarningsAsJson(CapturedOutput output) {
    try (var context = AppRunner.start("spring.profiles.active=prod")) {
      exercise(context);
    }
    assertThat(output.getOut())
        .contains("\"log\":{\"level\":\"WARN\",\"logger\":\"com.howtodoinjava.logging.playlist.PlaylistService\"}")
        .contains("\"message\":\"Cannot play 'gym': the playlist is empty\"")
        .doesNotContain("Adding 'Yesterday'")
        .doesNotContain("now has 1 songs");
  }

  @Test
  void consoleThresholdKeepsDebugOnlyInFile(CapturedOutput output, @TempDir Path dir) throws IOException {
    Path file = dir.resolve("playlist.log");
    try (var context = AppRunner.start("logging.file.name=" + file, "logging.threshold.console=warn")) {
      exercise(context);
    }
    assertThat(output.getOut()).doesNotContain("Adding 'Yesterday'").contains("Cannot play 'gym'");
    assertThat(Files.readString(file)).contains("Adding 'Yesterday' to playlist 'road-trip'");
  }

  @Test
  void ansiColorsWhenForced(CapturedOutput output) {
    try (var context = AppRunner.start("spring.output.ansi.enabled=always")) {
      exercise(context);
    }
    // ESC[31m = red for ERROR, ESC[33m = yellow for WARN, ESC[32m = green for INFO/DEBUG
    assertThat(output.getOut())
        .contains("\u001B[31mERROR\u001B[0;39m")
        .contains("\u001B[33m WARN\u001B[0;39m")
        .contains("\u001B[32m INFO\u001B[0;39m");
  }

  @Test
  void clrConversionWordInCustomPattern(CapturedOutput output) {
    try (var context = AppRunner.start("spring.output.ansi.enabled=always",
        "logging.pattern.console=%clr(%-5level) %clr(%logger{20}){cyan} - %msg%n")) {
      exercise(context);
    }
    assertThat(output.getOut())
        .contains("\u001B[31mERROR\u001B[0;39m \u001B[36mc.h.l.p.PlayerService\u001B[0;39m - Cannot play 'gym'");
  }

  @Test
  void consoleCanBeDisabled(CapturedOutput output, @TempDir Path dir) throws IOException {
    Path file = dir.resolve("playlist.log");
    try (var context = AppRunner.start("logging.file.name=" + file, "logging.console.enabled=false")) {
      exercise(context);
    }
    assertThat(output.getOut()).doesNotContain("Cannot play 'gym'");
    assertThat(Files.readString(file)).contains("Cannot play 'gym': the playlist is empty");
  }

  @Test
  void structuredJsonCustomized(CapturedOutput output) {
    try (var context = AppRunner.start("logging.structured.format.console=ecs",
        "logging.structured.json.exclude=process,service,ecs",
        "logging.structured.json.add.team=music")) {
      exercise(context);
    }
    assertThat(output.getOut()).containsPattern(
        "\\{\"@timestamp\":\"[^\"]+\",\"log\":\\{\"level\":\"ERROR\",\"logger\":\"com.howtodoinjava.logging.player.PlayerService\"},"
            + "\"message\":\"Cannot play 'gym': the playlist is empty\",\"team\":\"music\"}");
  }

  @Test
  void rollingPolicyArchivesFullFiles(CapturedOutput output, @TempDir Path dir) throws IOException {
    Path file = dir.resolve("playlist.log");
    try (var context = AppRunner.start("logging.file.name=" + file,
        "logging.logback.rollingpolicy.max-file-size=1KB",
        "logging.logback.rollingpolicy.file-name-pattern=" + dir + "/playlist-%d{yyyy-MM-dd}.%i.log.gz",
        "logging.threshold.console=off")) {
      PlaylistService playlists = context.getBean(PlaylistService.class);
      for (int i = 0; i < 50; i++) {
        playlists.addSong("road-trip", "Song " + i);
      }
    }
    try (Stream<Path> files = Files.list(dir)) {
      assertThat(files.map(p -> p.getFileName().toString()))
          .contains("playlist.log")
          .anyMatch(name -> name.matches("playlist-\\d{4}-\\d\\d-\\d\\d\\.0\\.log\\.gz"));
    }
    // logging.threshold.console=off: nothing on the console
    assertThat(output.getOut()).doesNotContain("now has");
  }

  @Test
  void logbackSpringXmlAddsErrorFile(CapturedOutput output) throws IOException {
    Path errors = Path.of("logs/errors.log");
    Files.deleteIfExists(errors);
    try (var context = AppRunner.start("logging.config=classpath:logback-errors-spring.xml")) {
      exercise(context);
    }
    String content = Files.readString(errors);
    assertThat(content).containsPattern("\\d\\d:\\d\\d:\\d\\d ERROR c\\.h\\.l\\.p\\.PlayerService - Cannot play 'gym': the playlist is empty");
    assertThat(content).doesNotContain("WARN").doesNotContain("INFO");
    // logging.level.com.howtodoinjava=debug from application.properties still applies
    assertThat(output.getOut()).contains("Adding 'Yesterday' to playlist 'road-trip'");
  }
}
