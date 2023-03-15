package com.howtodoinjava.demo.controller;

import com.howtodoinjava.demo.model.MovieDetails;
import com.howtodoinjava.demo.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MovieController {

    @Autowired
    private MovieService movieService;

    @PostMapping("/movies")
    public String addMovieDetails(@RequestBody MovieDetails movieDetails) {
        movieService.addMovieInfo(movieDetails);
        return "Success";
    }

    @GetMapping("/movies")
    public List<MovieDetails> getAllMovieDetails() {
        return movieService.fetchAllMovieDetails();
    }

    @DeleteMapping("/movie/{id}")
    public String deleteOneMovie(@PathVariable("id") String id) {
        movieService.deleteOneMovie(id);
        return "Success";
    }

}
