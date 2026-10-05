package com.howtodoinjava.playlist;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

@Service
public class SongService {

  private final List<Song> songs = new CopyOnWriteArrayList<>();
  private final AtomicLong ids = new AtomicLong();

  public SongService() {
    add("So What", "Miles Davis", 562, LocalDate.of(1959, 8, 17), 1250400, "JAZZ");
    add("Yellow", "Coldplay", 266, LocalDate.of(2000, 6, 26), 987654, "ROCK");
    add("Blinding Lights", "The Weeknd", 200, LocalDate.of(2019, 11, 29), 4500000, "POP");
  }

  public List<Song> findAll(String genre) {
    if (genre == null || genre.isBlank()) {
      return List.copyOf(songs);
    }
    return songs.stream().filter(s -> s.genre().equalsIgnoreCase(genre.strip())).toList();
  }

  public Optional<Song> findById(long id) {
    return songs.stream().filter(s -> s.id() == id).findFirst();
  }

  public Song add(SongForm form) {
    return add(form.getTitle().strip(), form.getArtist().strip(), form.getDurationSeconds(),
        form.getReleased(), 0, form.getGenre());
  }

  private Song add(String title, String artist, int seconds, LocalDate released, long plays,
                   String genre) {
    Song song = new Song(ids.incrementAndGet(), title, artist, seconds, released, plays, genre);
    songs.add(song);
    return song;
  }
}
