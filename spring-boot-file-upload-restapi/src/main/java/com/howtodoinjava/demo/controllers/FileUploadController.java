package com.howtodoinjava.demo.controllers;

import com.howtodoinjava.demo.model.FileForm;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class FileUploadController {

  @PostMapping("/upload")
  public String handleFileUpload(@ModelAttribute FileForm fileForm, Model model,
      RedirectAttributes redirectAttributes) throws IOException {

    if (fileForm.getFile().isEmpty()) {

      // Handle empty file error

      model.addAttribute("fileForm", fileForm);
      model.addAttribute("error", "File size is zero.");
    } else if (!fileForm.getFile().getOriginalFilename().endsWith(".txt")) {

      // Handle invalid file format error

      model.addAttribute("fileForm", fileForm);
      model.addAttribute("error", "Invalid file format. Only .txt files are allowed.");
    } else {

      //store the uploaded file

      redirectAttributes.addFlashAttribute("fileForm", new FileForm());
      redirectAttributes.addFlashAttribute("fileName", fileForm.getFile().getOriginalFilename());
      redirectAttributes.addFlashAttribute("fileSize", fileForm.getFile().getSize());
      redirectAttributes.addFlashAttribute("fileType", fileForm.getFile().getContentType());
      redirectAttributes.addFlashAttribute("success", "File uploaded successfully!");
    }
    return "redirect:/home";
  }

  @PostMapping("/single-file-upload")
  public ResponseEntity<Map<String, Object>> handleFileUploadUsingCurl(
      @RequestParam("file") MultipartFile file) throws IOException {

    Map<String, Object> response = new HashMap<>();
    if (file.isEmpty()) {
      // Handle empty file error
      response.put("error", "File is empty.");
      return ResponseEntity.badRequest().body(response);
    } else if (!file.getOriginalFilename().endsWith(".txt")) {
      // Handle invalid file format error
      response.put("error", "Invalid file format. Only .txt files are allowed.");
      return ResponseEntity.badRequest().body(response);
    } else {
      String sanitizedFileName = sanitizeFileName(file.getOriginalFilename());
      // Check if the sanitized file name is different from the original file name
      if (!sanitizedFileName.equals(file.getOriginalFilename())) {
        // Handle file name with restricted characters error
        response.put("error", "File name contains restricted characters. Please rename the file.");
        return ResponseEntity.badRequest().body(response);
      }

      // Populate the map with file details
      response.put("fileName", file.getOriginalFilename());
      response.put("fileSize", file.getSize());
      response.put("fileContentType", file.getContentType());

      // File upload is successful
      response.put("message", "File upload done");
      return ResponseEntity.ok(response);
    }
  }

  private String sanitizeFileName(String fileName) {
    String restrictedCharacters = "/\\:*?\"<>|";
    String sanitizedFileName = fileName.replaceAll("[" + restrictedCharacters + "]", "_");
    System.out.println(sanitizedFileName);
    return sanitizedFileName;
  }

  @GetMapping("/home")
  public String home(Model model) {
    model.addAttribute("fileForm", new FileForm());
    return "home";
  }

  @GetMapping("/")
  public String root() {
    return "redirect:/home";
  }

}
