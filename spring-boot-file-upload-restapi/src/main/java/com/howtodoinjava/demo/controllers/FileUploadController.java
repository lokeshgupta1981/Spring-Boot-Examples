package com.howtodoinjava.demo.controllers;

import com.howtodoinjava.demo.model.FileForm;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class FileUploadController {
    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, @RequestParam("name") String name, @RequestParam("email") String email, Model model) throws IOException {
        if (file.isEmpty()) {
            // Handle empty file error
            model.addAttribute("error", "File is empty.");
        } else if (!file.getOriginalFilename().endsWith(".txt")) {
            // Handle invalid file format error
            model.addAttribute("error", "Invalid file format. Only .txt files are allowed.");
        } else {
            model.addAttribute("success", "File uploaded successfully!");
            model.addAttribute("name", name);
            model.addAttribute("email", email);
            model.addAttribute("fileForm", new FileForm(file));
        }
        return "home";
    }

    @PostMapping("/uploadCurl")
    public ResponseEntity<Map<String, Object>> handleFileUploadUsingCurl(@RequestParam("file") MultipartFile file, Model model) throws IOException {
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

    @GetMapping("/")
    public String home(Model model) {
        return "home";
    }
}
