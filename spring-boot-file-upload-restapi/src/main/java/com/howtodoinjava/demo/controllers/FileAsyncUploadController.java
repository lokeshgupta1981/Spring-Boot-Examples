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
  public CompletableFuture<ResponseEntity<String>> handleConcurrentFileUpload(
      @RequestParam("file") MultipartFile file, Model model) throws IOException {

    // Handle empty file error
    if (file.isEmpty()) {
      return CompletableFuture
          .completedFuture(ResponseEntity.badRequest().body("File is empty."));
    }
    // Handle invalid file format error
    else if (!file.getOriginalFilename().endsWith(".txt")) {
      return CompletableFuture.completedFuture(
          ResponseEntity.badRequest().body("Invalid file format. Only .txt files are allowed."));
    }
    // File upload is successful
    else {
      String fileName = file.getOriginalFilename();
      //TODO: Additional processing if any
      return CompletableFuture.completedFuture(
          ResponseEntity.ok("File uploaded: " + fileName + "\n"));
    }
  }
}
