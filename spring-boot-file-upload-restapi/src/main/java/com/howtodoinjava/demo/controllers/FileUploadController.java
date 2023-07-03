package com.howtodoinjava.demo.controllers;

import com.howtodoinjava.demo.model.FileForm;
import com.howtodoinjava.demo.model.User;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class FileUploadController {

  @PostMapping("/upload")
  public String handleFileUpload(@RequestParam("file") MultipartFile file,
      @ModelAttribute("user") User user, Model model) throws IOException {

    if (file.isEmpty()) {
      model.addAttribute("error", "File is empty.");
    } else if (!file.getOriginalFilename().endsWith(".txt")) {
      model.addAttribute("error", "Invalid file format. Only .txt files are allowed.");
    } else {
      model.addAttribute("success", "File uploaded successfully!");
      model.addAttribute("fileForm", new FileForm(file));
      model.addAttribute("user", user);
    }
    return "home";
  }

  @PostMapping("/uploadMultipleForm")
  public String handleFileUploadMultiple(@RequestParam("files") MultipartFile[] files, Model model)
      throws IOException {

    if (files.length == 0) {
      model.addAttribute("error", "No files selected");

    }
    List<FileForm> fileForms = new ArrayList<>();
    for (MultipartFile file : files) {
      // Handle each file as needed
      // Example: Save the file to disk, process its contents, etc.
      fileForms.add(new FileForm(file));
    }
    model.addAttribute("success", "Files uploaded successfully!");
    model.addAttribute("fileForms", fileForms);
    return "home";
  }

  @PostMapping("/uploadMultiple")
  public ResponseEntity<String> handleFileUploadMultipleCurl(
      @RequestParam("files") MultipartFile[] files, Model model) throws IOException {

    if (files.length == 0) {
      model.addAttribute("error", "No files selected");
    }

    List<FileForm> fileForms = new ArrayList<>();
    for (MultipartFile file : files) {
      // Handle each file as needed
      // Example: Save the file to disk, process its contents, etc.
      fileForms.add(new FileForm(file));
    }

    model.addAttribute("success", "Files uploaded successfully!");
    model.addAttribute("fileForms", fileForms);
    return ResponseEntity.ok("Files uploaded successfully!");
  }

  @PostMapping("/uploadCurl")
  public ResponseEntity<String> handleFileUploadUsingCurl(@RequestParam("file") MultipartFile file,
      Model model) throws IOException {
    if (file.isEmpty()) {
      return ResponseEntity.badRequest().body("File is empty.");

    } else if (!file.getOriginalFilename().endsWith(".txt")) {
      return ResponseEntity.badRequest().body("Invalid file format. Only .txt files are allowed.");
    } else {
      String sanitizedFileName = sanitizeFileName(file.getOriginalFilename());
      if (!sanitizedFileName.equals(file.getOriginalFilename())) {
        return ResponseEntity.badRequest()
            .body("File name contains restricted characters. Please rename the file.");
      }
      return ResponseEntity.ok("file upload done");
    }
  }

  @Async
  @PostMapping("/uploadAsync")
  public CompletableFuture<ResponseEntity<String>> handleConcurrentFileUpload(
      @RequestParam("file") MultipartFile file, Model model) throws IOException {
    if (file.isEmpty()) {
      return CompletableFuture.completedFuture(ResponseEntity.badRequest().body("File is empty."));
    } else if (!file.getOriginalFilename().endsWith(".txt")) {
      return CompletableFuture.completedFuture(
          ResponseEntity.badRequest().body("Invalid file format. Only .txt files are allowed."));
    } else {
      String fileName = file.getOriginalFilename();
      return CompletableFuture.completedFuture(
          ResponseEntity.ok("File uploaded: " + fileName + "\n"));
    }
  }

  private String sanitizeFileName(String fileName) {
    String restrictedCharacters = "/\\:*?\"<>|";
    String sanitizedFileName = fileName.replaceAll("[" + restrictedCharacters + "]", "_");
    System.out.println(sanitizedFileName);
    return sanitizedFileName;
  }


  @GetMapping("/")
  public String home(Model model) {
    model.addAttribute("user", new User());
    return "home";
  }
}
