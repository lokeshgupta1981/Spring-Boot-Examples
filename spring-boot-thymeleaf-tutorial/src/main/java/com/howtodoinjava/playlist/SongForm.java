package com.howtodoinjava.playlist;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public class SongForm {

  @NotBlank
  @Size(max = 40)
  private String title;

  @NotBlank
  private String artist;

  @NotNull
  @Min(30)
  @Max(1200)
  private Integer durationSeconds;

  @NotNull
  @PastOrPresent
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate released;

  @NotBlank
  private String genre = "POP";

  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }

  public String getArtist() { return artist; }
  public void setArtist(String artist) { this.artist = artist; }

  public Integer getDurationSeconds() { return durationSeconds; }
  public void setDurationSeconds(Integer durationSeconds) { this.durationSeconds = durationSeconds; }

  public LocalDate getReleased() { return released; }
  public void setReleased(LocalDate released) { this.released = released; }

  public String getGenre() { return genre; }
  public void setGenre(String genre) { this.genre = genre; }
}
