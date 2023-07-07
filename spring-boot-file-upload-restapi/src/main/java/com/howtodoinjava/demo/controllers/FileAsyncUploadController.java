package com.howtodoinjava.demo.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Controller
public class FileAsyncUploadController {
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
}
