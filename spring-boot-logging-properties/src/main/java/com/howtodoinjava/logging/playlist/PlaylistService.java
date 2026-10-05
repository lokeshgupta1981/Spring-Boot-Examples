package com.howtodoinjava.logging.playlist;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Keeps playlists in memory and writes one log statement per log level.
 */
@Service
public class PlaylistService {

  private static final Logger log = LoggerFactory.getLogger(PlaylistService.class);

  private final Map<String, List<String>> playlists = new ConcurrentHashMap<>();

  public int addSong(String playlist, String song) {
    log.trace("Checking song '{}'", song);
    log.debug("Adding '{}' to playlist '{}'", song, playlist);
    List<String> songs = playlists.computeIfAbsent(playlist, name -> new ArrayList<>());
    if (songs.contains(song)) {
      log.warn("'{}' is already in playlist '{}'", song, playlist);
      return songs.size();
    }
    songs.add(song);
    log.info("Playlist '{}' now has {} songs", playlist, songs.size());
    return songs.size();
  }

  public List<String> songs(String playlist) {
    return playlists.getOrDefault(playlist, List.of());
  }
}
