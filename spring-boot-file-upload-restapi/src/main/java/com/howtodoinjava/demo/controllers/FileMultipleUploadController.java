package com.howtodoinjava.demo.controllers;

import com.howtodoinjava.demo.model.FileForm;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class FileMultipleUploadController {

  @PostMapping("/uploadMultiple")
  public ResponseEntity<String> handleFileUploadMultipleCurl(
      @RequestParam("files") MultipartFile[] files, Model model) throws IOException {

    // Check if any files are provided
    if (files.length == 0) {
      model.addAttribute("error", "No files selected");
      return ResponseEntity.badRequest().body("Files list is empty");
    }

    List<FileForm> fileForms = new ArrayList<>();
    for (MultipartFile file : files) {
      // Handle each file as needed
      // Example: Save the file to disk, process its contents, etc.
      fileForms.add(new FileForm(null, null, file));
    }
    model.addAttribute("success", "Files uploaded successfully!");
    return ResponseEntity.ok("Files uploaded successfully!");
  }
}
