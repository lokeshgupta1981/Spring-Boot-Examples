package com.springapi.uploading.controller;

import com.springapi.uploading.model.Product;
import com.springapi.uploading.service.ProductService;
import com.springapi.uploading.service.ResponseClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

//@RestController
//@RequestMapping("/api/files")
@Controller
public class ProductController {
    @Autowired
    private ProductService fileService;

    // for uploading the SINGLE file to the database
//    @PostMapping("/single/base")
//    public ResponseClass uploadFile(@RequestParam("file") MultipartFile file) throws Exception {
//
//        Product attachment = null;
//        String downloadURl = "";
//        attachment = fileService.saveAttachment(file);
//        downloadURl = ServletUriComponentsBuilder.fromCurrentContextPath()
//                .path("/download/")
//                .path(attachment.getId())
//                .toUriString();
//
//        return new ResponseClass(attachment.getFileName(),
//                downloadURl,
//                file.getContentType(),
//                file.getSize());
//    }
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


    //for uploading the MULTIPLE files to the database
//    @PostMapping("/multiple/base")
//    public List<ResponseClass> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) throws Exception {
//        List<ResponseClass> responseList = new ArrayList<>();
//        for (MultipartFile file : files) {
//            Product attachment = fileService.saveAttachment(file);
//            String downloadUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
//                    .path("/download/")
//                    .path(attachment.getId())
//                    .toUriString();
//            ResponseClass response = new ResponseClass(attachment.getFileName(),
//                    downloadUrl,
//                    file.getContentType(),
//                    file.getSize());
//            responseList.add(response);
//        }
//        return responseList;
//    }
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
//For HTML FORM
@GetMapping("/")
public String getData() {
    return "File";
}

    @PostMapping("/")
    public String uploadMultipartFile(@RequestParam("files") MultipartFile[] files, Model modal) {
        try {
            // Declare empty list for collect the files data
            // which will come from UI
            List<Product> fileList = new ArrayList<Product>();
            for (MultipartFile file : files) {
                String fileContentType = file.getContentType();
                byte[] sourceFileContent = file.getBytes();
                String fileName = file.getOriginalFilename();
                Product fileModal = new Product(fileName, fileContentType, sourceFileContent);

                // Adding file into fileList
                fileList.add(fileModal);
            }
            fileService.saveAllFilesList(fileList);

        } catch (Exception e) {
            e.printStackTrace();
        }
        modal.addAttribute("allFiles", fileService.getAllFiles());

        return "FileList";
    }

}