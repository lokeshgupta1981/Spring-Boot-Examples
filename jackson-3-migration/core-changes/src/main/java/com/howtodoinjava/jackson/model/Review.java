package com.howtodoinjava.jackson.model;

import com.fasterxml.jackson.annotation.JsonView;

/** Only "stars" belongs to the Public view; "author" has no view annotation. */
public class Review {

  public interface Public {
  }

  @JsonView(Public.class)
  private int stars;

  private String author;

  public Review() {
  }

  public static Review of(int stars, String author) {
    Review review = new Review();
    review.stars = stars;
    review.author = author;
    return review;
  }

  public int getStars() {
    return stars;
  }

  public String getAuthor() {
    return author;
  }
}
