package com.howtodoinjava.controller;

import com.howtodoinjava.service.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@Controller
@Slf4j
@RequestMapping("/image")
public class ImageController {

    @Autowired
    private ImageService imageService;

    @GetMapping
    public String home() {

        return "redirect:/image/view";

    }

    @GetMapping("/view")
    public String view(Model model) {

        model.addAttribute("images", imageService.getAll());

        return "view";

    }

    @GetMapping("/upload")
    public String upload() {

        return "upload";

    }

    @PostMapping(value = "/upload", consumes = MULTIPART_FORM_DATA_VALUE)
    public String handleImageUpload(@RequestPart("file") MultipartFile file) {

        log.info("handling request parts: {}", file);

        imageService.save(file);

        return "redirect:/image/view";

    }

    @GetMapping("/get/{filename:.+}")
    public ResponseEntity<Resource> load(@PathVariable String filename) {

        Resource resource = imageService.load(filename);

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"").body(resource);

    }


}
