/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validateLists;

import jakarta.validation.constraints.Min;

/* JSON example:

{
  "brand": "BMW",
  "price": 5
}
 
 */
public class Car {

    private String brand;

    @Min(value = 10, message = "Price must be bigger than 10")
    private Integer price;

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

}
