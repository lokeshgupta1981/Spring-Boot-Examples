package com.howtodoinjava.elasticsearch;

import java.util.List;

/** The eight listings used in the article and the tests. */
public final class CarData {

  private CarData() {
  }

  public static List<CarListing> listings() {
    return List.of(
        new CarListing("1", "Toyota", "Corolla", 2019, 15000, 42000, "One owner, full service history, new tires.", "Austin"),
        new CarListing("2", "Toyota", "Camry", 2021, 22000, 30000, "Hybrid with leather seats and a backup camera.", "Denver"),
        new CarListing("3", "Honda", "Civic", 2018, 13500, 61000, "Manual gearbox, new brakes, clean title.", "Austin"),
        new CarListing("4", "Honda", "Accord", 2020, 19500, 38000, "Hybrid with low mileage and a sunroof.", "Seattle"),
        new CarListing("5", "Ford", "Focus", 2017, 9000, 85000, "Small city car with a few scratches on the bumper.", "Denver"),
        new CarListing("6", "Ford", "Mustang", 2022, 34000, 12000, "V8 engine, leather seats, like new.", "Austin"),
        new CarListing("7", "Tesla", "Model 3", 2021, 27000, 25000, "Electric car with autopilot and a white interior.", "Seattle"),
        new CarListing("8", "BMW", "X3", 2019, 26000, 48000, "All wheel drive with heated leather seats.", "Denver"));
  }
}
