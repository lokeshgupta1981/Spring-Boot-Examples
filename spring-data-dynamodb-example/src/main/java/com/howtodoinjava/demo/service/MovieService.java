package com.howtodoinjava.demo.service;

import com.howtodoinjava.demo.model.MovieDetails;

import java.util.List;

public interface MovieService {

    void addMovieInfo(MovieDetails movieDetails);

    List<MovieDetails> fetchAllMovieDetails();

    void deleteOneMovie(String id);
}
