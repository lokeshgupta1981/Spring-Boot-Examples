/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.howtodoinjava.app.model;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/* JSON example:
{
  "brand": "BMW",
  "price": 5
}
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Car {

  private String brand;

  @Min(value = 10, message = "Price must be greater than 10")
  private Integer price;
}
