package com.howtodoinjava.demo.service;

import com.howtodoinjava.demo.model.MovieDetail;

import java.util.List;

public interface MovieService {

  MovieDetail addMovieInfo(MovieDetail movieDetail);

  List<MovieDetail> fetchAllMovieDetails();

  void deleteOneMovie(String id);
}
