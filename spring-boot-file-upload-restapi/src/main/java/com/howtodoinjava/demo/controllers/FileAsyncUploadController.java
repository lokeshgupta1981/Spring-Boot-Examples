package com.howtodoinjava.demo.controllers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Random;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class FileAsyncUploadController {

  @Autowired
  FileStorageManager fileStorageManager;

  @PostMapping("/uploadAsync")
  public ResponseEntity<String> handleConcurrentFilesUpload(
      @RequestParam("files") MultipartFile[] files) throws IOException {

    // Handle empty file error
    if (files.length == 0) {
      return ResponseEntity.badRequest().body("No files submitted");
    }
    // File upload process is submitted
    else {
      for (MultipartFile file : files) {
        fileStorageManager.save(file);
        //TODO: access and store each file into file storage
      }
      return ResponseEntity.ok("File upload started");
    }
  }
}

@Service
class FileStorageManager {

  @SneakyThrows
  @Async
  public void save(MultipartFile file) {
    Thread.sleep(new Random().nextLong(4000, 8000));
    System.out.println(file.getOriginalFilename() + " is uploaded at " + LocalDateTime.now());
  }
}
