/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.howtodoinjava.app.web;

import com.howtodoinjava.app.model.Car;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/car")
@Validated
public class CarController {

    @PostMapping
    public ResponseEntity<String> createCar(@Validated @RequestBody Car car) {

        return ResponseEntity.ok("Car created successfully");
    }
}