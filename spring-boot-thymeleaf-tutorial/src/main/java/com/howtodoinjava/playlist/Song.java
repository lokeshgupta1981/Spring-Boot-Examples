package com.howtodoinjava.playlist;

import java.time.LocalDate;

public record Song(long id, String title, String artist, int durationSeconds,
                   LocalDate released, long plays, String genre) {
}
