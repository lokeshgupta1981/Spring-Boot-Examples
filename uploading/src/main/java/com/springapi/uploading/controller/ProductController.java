package com.springapi.uploading.controller;

import com.springapi.uploading.model.Product;
import com.springapi.uploading.service.ProductService;
import com.springapi.uploading.service.ResponseClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

//@RestController
//@RequestMapping("/api/files")
@Controller
public class ProductController {
    @Autowired
    private ProductService fileService;

    @PostMapping("/single/base")
    public ResponseEntity<ResponseClass> uploadFile(@RequestParam("file") MultipartFile file) throws Exception {
        Product attachment = null;
        attachment = fileService.saveAttachment(file);
        String fileName = file.getOriginalFilename();
        try {
            String downloadUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/download/")
                    .path(fileName)
                    .toUriString();
            ResponseClass response = new ResponseClass(fileName,
                    downloadUrl,
                    file.getContentType(),
                    file.getSize());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping("/multiple/base")
    public ResponseEntity<List<ResponseClass>> handleMultipleFilesUploads(@RequestParam("files") MultipartFile[] files) throws Exception {
        List<ResponseClass> responseList = new ArrayList<>();
        for (MultipartFile file : files) {
            Product attachment = fileService.saveAttachment(file);
            String fileName = file.getOriginalFilename();


            try {

                String downloadUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/download/")
                        .path(fileName)
                        .toUriString();
                ResponseClass response = new ResponseClass(fileName,
                        downloadUrl,
                        file.getContentType(),
                        file.getSize());
                responseList.add(response);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
        return ResponseEntity.ok(responseList);
    }
    //for retrieving  all the  files uploaded
    @GetMapping("/all")
    public ResponseEntity<List<ResponseClass>> getAllFiles() {
        List<Product> products = fileService.getAllFiles();
        List<ResponseClass> responseClasses = products.stream().map(product -> {
            String downloadURL = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/download/")
                    .path(product.getId())
                    .toUriString();
            return new ResponseClass(product.getFileName(),
                    downloadURL,
                    product.getFileType(),
                    product.getData().length);
        }).collect(Collectors.toList());

        return ResponseEntity.ok().body(responseClasses);
    }
    //for uploading the SINGLE file to the File System
    @PostMapping("/single/file")
    public ResponseEntity<ResponseClass> handleFileUpload(@RequestParam("file") MultipartFile file) {
        String fileName = file.getOriginalFilename();
        try {
            file.transferTo(new File("D:\\Folder\\" + fileName));
            String downloadUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/download/")
                    .path(fileName)
                    .toUriString();
            ResponseClass response = new ResponseClass(fileName,
                    downloadUrl,
                    file.getContentType(),
                    file.getSize());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //for uploading the MULTIPLE file to the File system
    @PostMapping("/multiple/file")
    public ResponseEntity<List<ResponseClass>> handleMultipleFilesUpload(@RequestParam("files") MultipartFile[] files) {
        List<ResponseClass> responseList = new ArrayList<>();
        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            try {
                file.transferTo(new File("D:\\Folder\\" + fileName));
                String downloadUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/download/")
                        .path(fileName)
                        .toUriString();
                ResponseClass response = new ResponseClass(fileName,
                        downloadUrl,
                        file.getContentType(),
                        file.getSize());
                responseList.add(response);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/")
public String getData() {
    return "uploadform";
}

    @PostMapping("/submit")
    public String submitForm1(@RequestParam("name") String name,
                             @RequestParam("email") String email,
                             @RequestParam("phone") String phone,
                             @RequestParam("address") String address,
                             @RequestParam("files") MultipartFile[] files,
                             Model model) throws IOException {
        List<String> filenames = new ArrayList<>();
        for (MultipartFile file : files) {
            String filename = file.getOriginalFilename();
            file.transferTo(new File("D:\\Folder\\" + filename));
            filenames.add(filename);
        }
        String downloadUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/download/")
                .path(filenames.get(0))
                .toUriString();

// Populate the model with the form data and uploaded files
        model.addAttribute("name", name);
        model.addAttribute("email", email);
        model.addAttribute("phone", phone);
        model.addAttribute("address", address);
        model.addAttribute("filenames", filenames);
        model.addAttribute("fileUrl", downloadUrl);
        return "success";

    }
}