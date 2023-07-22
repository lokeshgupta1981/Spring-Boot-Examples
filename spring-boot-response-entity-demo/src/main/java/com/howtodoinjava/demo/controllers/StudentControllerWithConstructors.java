package com.howtodoinjava.demo.controllers;

import com.howtodoinjava.demo.entity.Student;
import com.howtodoinjava.demo.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/students")
public class StudentControllerWithConstructors {
    @Autowired
    private StudentService studentService;

    //Creating a response with body and status code
    @PostMapping("/create")
    public ResponseEntity<Student> createStudent(@RequestBody Student student){
        studentService.createStudent(student);
        return new ResponseEntity<>(student,HttpStatus.CREATED);
    }


    @GetMapping("/all")
    public ResponseEntity<List<Student>> getAllStudents() {
        List<Student> students = studentService.getAllStudents();

        if (students != null) {
            // Return student with HTTP status 200 OK
            return new ResponseEntity<>(students, HttpStatus.OK);
        } else {
            // Return 404 Not Found
            //Creating a response with only status code
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    //Creating a response with headers and status code
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable long id) {
        // Delete student by ID
        Optional<Student> studentDeleted = studentService.getStudentById(id);
        if (studentDeleted.isPresent()) {
            studentService.deleteStudent(id);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Custom-Header", "Deleted");
            return new ResponseEntity<>(headers, HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    //Creating a response with headers, body, and status code:
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable long id) {
        Optional<Student> student = studentService.getStudentById(id);
        if (student.isPresent()) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Custom-Header", "Found");
            return new ResponseEntity<>(student.get(), headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
