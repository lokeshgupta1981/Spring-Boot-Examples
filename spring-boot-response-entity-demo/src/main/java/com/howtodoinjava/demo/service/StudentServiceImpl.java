package com.howtodoinjava.demo.service;

import com.howtodoinjava.demo.entity.Student;
import com.howtodoinjava.demo.exception.ResourceNotFoundException;
import com.howtodoinjava.demo.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentServiceImpl implements StudentService{

    @Autowired
    private StudentRepository studentRepository;

        @Override
        public List<Student> getAllStudents() {
            return studentRepository.findAll();
        }

        @Override
        public Optional<Student> getStudentById(Long id) {
            return studentRepository.findById(id);
        }

        @Override
        public Student createStudent(Student student) {
            return studentRepository.save(student);
        }


    @Override
    public Student updateStudent(Long id, Student updatedStudent) {
        Optional<Student> student = getStudentById(id);
        student.get().setName(updatedStudent.getName());
        student.get().setAge(updatedStudent.getAge());
        return studentRepository.save(student.get());
    }


    @Override
        public void deleteStudent(Long id) {
            studentRepository.deleteById(id);
        }
    }

