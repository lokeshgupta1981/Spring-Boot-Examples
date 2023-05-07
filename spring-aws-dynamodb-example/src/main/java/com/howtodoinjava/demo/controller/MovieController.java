package com.howtodoinjava.demo.controller;

import com.howtodoinjava.demo.entity.MovieDetails;
import com.howtodoinjava.demo.service.DynamoDbOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;

import java.util.List;

@RestController
public class MovieController {

    @Autowired
    private DynamoDbOperationService dbOperationService;

    @PostMapping("/movie")
    public MovieDetails saveMovie(@RequestBody MovieDetails movieDetails) {
       return dbOperationService.saveData(movieDetails);
    }

    @PutMapping("/movie")
    public MovieDetails updateMovie(@RequestBody MovieDetails movieDetails) {
        return dbOperationService.updateData(movieDetails);
    }

    @GetMapping("/movie/{id}")
    public MovieDetails findById(@PathVariable("id") String id) {
        return dbOperationService.findById(id);
    }

    @DeleteMapping("/movie/{id}")
    public void deleteById(@PathVariable("id") String id) {
        dbOperationService.deleteById(id);
    }

    @GetMapping("/movie")
    public List<MovieDetails> getMovieListByGenre(@RequestParam("genre") String genre) {
        return dbOperationService.scanDataByGenre(genre);
    }
}
