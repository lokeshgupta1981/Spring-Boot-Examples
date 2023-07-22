package com.howtodoinjava.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@RestController
public class TestController {

    @GetMapping("/user/{id}")
    public String getUserById(@PathVariable String id) {
        return "User Id: " + id;
    }

    @GetMapping("/users")
    public String getUsersByName(@RequestParam String name) {
        return "User Name: " + name;
    }

    @GetMapping("/user/optional/{id}")
    public String getUserByIdOptional(@PathVariable(required = false) String id) {
        return "User Id: " + id;
    }

    @GetMapping("/users/optional")
    public String getUsersByNameOptional(@RequestParam(required = false) String name) {
        return "User Name: " + name;
    }

    /*
    @GET
    @Path("/user/{id}")
    public Response getUserById(@PathParam("id") String id) {
        return Response.ok().build();
    }
     */

    /*
    @GET
    @Path("/users")
    public Response getUsers(@QueryParam("name") String name) {
        // Method implementation
    }
     */

}
