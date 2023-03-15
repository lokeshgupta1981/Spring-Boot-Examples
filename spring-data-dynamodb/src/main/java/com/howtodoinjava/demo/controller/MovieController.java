package com.howtodoinjava.demo.controller;

import com.howtodoinjava.demo.model.MovieDetail;
import com.howtodoinjava.demo.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
public class MovieController {

  @Autowired
  private MovieService movieService;

  @PostMapping("/movies")
  public ResponseEntity addMovieDetails(@RequestBody MovieDetail movieDetail) {
    movieDetail = movieService.addMovieInfo(movieDetail);
    return ResponseEntity.created(URI.create("/" + movieDetail.getId())).build();
  }

  @GetMapping("/movies")
  public List<MovieDetail> getAllMovieDetails() {
    return movieService.fetchAllMovieDetails();
  }

  @DeleteMapping("/movie/{id}")
  public ResponseEntity deleteOneMovie(@PathVariable("id") String id) {
    movieService.deleteOneMovie(id);
    return ResponseEntity.accepted().build();
  }

}
