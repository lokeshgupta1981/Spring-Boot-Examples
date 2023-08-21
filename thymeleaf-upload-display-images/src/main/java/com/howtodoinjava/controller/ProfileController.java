package com.howtodoinjava.controller;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

import com.howtodoinjava.model.Profile;
import com.howtodoinjava.service.ProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

@Controller
@Slf4j
public class ProfileController {

  @Autowired
  private ProfileService profileService;

  @GetMapping("/")
  public String home() {

    return "redirect:/profiles/view";
  }

  @GetMapping(value = "/images/{imageId}", produces = MediaType.IMAGE_JPEG_VALUE)
  public Resource downloadImage(@PathVariable Long imageId) {
    byte[] image = profileService.findById(imageId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND))
        .getImageData();

    return new ByteArrayResource(image);
  }

  @GetMapping("/profiles/view")
  public ModelAndView view(Model model) {

    return new ModelAndView("view", "profiles", profileService.getAll());
  }

  @GetMapping("/profiles/add")
  public ModelAndView addProfile() {

    return new ModelAndView("addProfile", "profile", new Profile());
  }

  @PostMapping(value = "/profiles/add", consumes = MULTIPART_FORM_DATA_VALUE)
  public String handleProfileAdd(Profile profile, @RequestPart("file") MultipartFile file) {

    log.info("handling request parts: {}", file);
    profileService.save(profile, file);
    return "redirect:/profiles/view";
  }
}
