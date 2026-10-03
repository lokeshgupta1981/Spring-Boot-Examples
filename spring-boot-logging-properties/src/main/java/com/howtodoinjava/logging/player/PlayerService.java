package com.howtodoinjava.logging.player;

import com.howtodoinjava.logging.playlist.PlaylistService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Plays a playlist; logs an ERROR when the playlist is empty.
 */
@Service
public class PlayerService {

  private static final Logger log = LoggerFactory.getLogger(PlayerService.class);

  private final PlaylistService playlistService;

  public PlayerService(PlaylistService playlistService) {
    this.playlistService = playlistService;
  }

  public String play(String playlist) {
    log.debug("Loading playlist '{}'", playlist);
    List<String> songs = playlistService.songs(playlist);
    if (songs.isEmpty()) {
      log.error("Cannot play '{}': the playlist is empty", playlist);
      return "empty";
    }
    log.info("Playing '{}' from '{}'", songs.getFirst(), playlist);
    return songs.getFirst();
  }
}
