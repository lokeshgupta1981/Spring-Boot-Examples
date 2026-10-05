package com.howtodoinjava.elasticsearch;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

@Document(indexName = "car-listings")
@Setting(shards = 1, replicas = 0)
public class CarListing {

  @Id
  private String id;

  @Field(type = FieldType.Keyword)
  private String make;

  @Field(type = FieldType.Keyword)
  private String model;

  @Field(type = FieldType.Integer)
  private int year;

  @Field(type = FieldType.Integer)
  private int price;

  @Field(type = FieldType.Integer)
  private int mileage;

  @Field(type = FieldType.Text, analyzer = "english")
  private String description;

  @Field(type = FieldType.Keyword)
  private String city;

  public CarListing() {
  }

  public CarListing(String id, String make, String model, int year, int price, int mileage,
                    String description, String city) {
    this.id = id;
    this.make = make;
    this.model = model;
    this.year = year;
    this.price = price;
    this.mileage = mileage;
    this.description = description;
    this.city = city;
  }

  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
  public String getMake() { return make; }
  public void setMake(String make) { this.make = make; }
  public String getModel() { return model; }
  public void setModel(String model) { this.model = model; }
  public int getYear() { return year; }
  public void setYear(int year) { this.year = year; }
  public int getPrice() { return price; }
  public void setPrice(int price) { this.price = price; }
  public int getMileage() { return mileage; }
  public void setMileage(int mileage) { this.mileage = mileage; }
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
  public String getCity() { return city; }
  public void setCity(String city) { this.city = city; }

  /** Short label used in the article results, for example "Toyota Corolla 2019". */
  public String label() {
    return make + " " + model + " " + year;
  }

  @Override
  public String toString() {
    return label() + " (" + price + ", " + city + ")";
  }
}
