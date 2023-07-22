package com.howtodoinjava.demo.controllers;

import com.howtodoinjava.demo.entity.Student;
import com.howtodoinjava.demo.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/studentsBuilders")
public class StudentControllerWithBuilders {

    @Autowired
    private StudentService studentService;

    @PostMapping("/create")
    public ResponseEntity<Student> createStudent(@RequestBody Student student) {
        studentService.createStudent(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(student);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Student>> getAllStudents() {
        List<Student> students = studentService.getAllStudents();

        if (students != null) {
            return ResponseEntity.ok(students); // Return student with HTTP status 200 OK
        } else {
            return ResponseEntity.notFound().build(); // Return 404 Not Found
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable long id) {
        Optional<Student> studentDeleted = studentService.getStudentById(id);
        if (studentDeleted.isPresent()) {
            studentService.deleteStudent(id);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Custom-Header", "Deleted");
            return ResponseEntity.noContent().headers(headers).build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable long id) {
        Optional<Student> student = studentService.getStudentById(id);
        if (student.isPresent()) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Custom-Header", "Found");
            return ResponseEntity.ok().headers(headers).body(student.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
