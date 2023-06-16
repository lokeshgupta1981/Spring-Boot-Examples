package com.howtodoinjava.demo.controllers;

import com.howtodoinjava.demo.entities.FileForm;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Controller
public class FileUploadController {
    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, Model model) throws IOException {
        if (file.isEmpty()) {
            // Handle empty file error
            model.addAttribute("error", "File is empty.");
        } else if (!file.getOriginalFilename().endsWith(".txt")) {
            // Handle invalid file format error
            model.addAttribute("error", "Invalid file format. Only .txt files are allowed.");
        } else {
            // File upload is successful
            model.addAttribute("success", "File uploaded successfully!");
            model.addAttribute("fileForm", new FileForm(file));
        }

        return "home";
    }

    @PostMapping("/uploadMultiple")
    public String handleFileUploadMultiple(@RequestParam("files") MultipartFile[] files, Model model) throws IOException {
        // Check if any files are provided
        if (files.length == 0) {
            model.addAttribute("error", "No files selected");

        }
        List<FileForm> fileForms = new ArrayList<>();

        // Process each file
        for (MultipartFile file : files) {
            // Handle each file as needed
            // Example: Save the file to disk, process its contents, etc.
            fileForms.add(new FileForm(file));
        }
        model.addAttribute("success", "Files uploaded successfully!");
        model.addAttribute("fileForms", fileForms);

        // Return a success message
        return "home";
    }

    @PostMapping("/uploadCurl")
    public ResponseEntity<String> handleFileUploadUsingCurl(@RequestParam("file") MultipartFile file, Model model) throws IOException {
        if (file.isEmpty()) {
            // Handle empty file error
            return ResponseEntity.badRequest().body("File is empty.");

        } else if (!file.getOriginalFilename().endsWith(".txt")) {
            // Handle invalid file format error
            return ResponseEntity.badRequest().body("Invalid file format. Only .txt files are allowed.");
        } else {
            // File upload is successful
            return ResponseEntity.ok("file upload done");        }

    }

    @Async
    @PostMapping("/uploadAsync")
    public CompletableFuture<ResponseEntity<String>> handleConcurrentFileUpload(@RequestParam("file") MultipartFile file, Model model) throws IOException {
        if (file.isEmpty()) {
            // Handle empty file error
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body("File is empty."));
        } else if (!file.getOriginalFilename().endsWith(".txt")) {
            // Handle invalid file format error
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body("Invalid file format. Only .txt files are allowed."));
        } else {
            // File upload is successful
            String fileName = file.getOriginalFilename();
            // Additional processing or logging with the fileName
            return CompletableFuture.completedFuture(ResponseEntity.ok("File uploaded: " + fileName + "\n"));
        }
    }


    @GetMapping("/")
    public String index() {
        return "home";
    }
}
