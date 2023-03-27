package com.howtodoinjava.demo.service.impl;

import com.howtodoinjava.demo.model.MovieDetail;
import com.howtodoinjava.demo.repositories.MovieDetailRepository;
import com.howtodoinjava.demo.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieServiceImpl implements MovieService {

  @Autowired
  private MovieDetailRepository movieDetailsRepository;

  @Override
  public MovieDetail addMovieInfo(MovieDetail movieDetail) {
    return movieDetailsRepository.save(movieDetail);
  }

  @Override
  public List<MovieDetail> fetchAllMovieDetails() {
    return (List<MovieDetail>) movieDetailsRepository.findAll();
  }

  @Override
  public void deleteOneMovie(String id) {
    movieDetailsRepository.deleteById(id);
  }
}