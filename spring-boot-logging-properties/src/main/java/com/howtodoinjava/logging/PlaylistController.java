package com.howtodoinjava.logging;

import com.howtodoinjava.logging.player.PlayerService;
import com.howtodoinjava.logging.playlist.PlaylistService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Two endpoints that trigger the log statements:
 * POST /playlists/{name}/songs?title=... and POST /playlists/{name}/play.
 */
@RestController
public class PlaylistController {

  private final PlaylistService playlistService;
  private final PlayerService playerService;

  public PlaylistController(PlaylistService playlistService, PlayerService playerService) {
    this.playlistService = playlistService;
    this.playerService = playerService;
  }

  @PostMapping("/playlists/{name}/songs")
  public int addSong(@PathVariable String name, @RequestParam String title) {
    return playlistService.addSong(name, title);
  }

  @PostMapping("/playlists/{name}/play")
  public String play(@PathVariable String name) {
    return playerService.play(name);
  }
}
