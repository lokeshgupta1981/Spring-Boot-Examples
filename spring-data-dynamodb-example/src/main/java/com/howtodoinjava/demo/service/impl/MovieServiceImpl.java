package com.howtodoinjava.demo.service.impl;

import com.howtodoinjava.demo.model.MovieDetails;
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
    public void addMovieInfo(MovieDetails movieDetails) {
        movieDetailsRepository.save(movieDetails);
    }

    @Override
    public List<MovieDetails> fetchAllMovieDetails() {
        return (List<MovieDetails>) movieDetailsRepository.findAll();
    }

    @Override
    public void deleteOneMovie(String id) {
        movieDetailsRepository.deleteById(id);
    }
}