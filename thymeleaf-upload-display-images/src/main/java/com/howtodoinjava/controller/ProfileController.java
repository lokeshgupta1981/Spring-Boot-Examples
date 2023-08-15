package com.howtodoinjava.controller;

import com.howtodoinjava.model.Profile;
import com.howtodoinjava.service.ProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@Controller
@Slf4j
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @GetMapping("/")
    public String home() {

        return "redirect:/profiles/view";

    }

    @GetMapping("/profiles/view")
    public ModelAndView view(Model model) {

        return new ModelAndView("view", "profiles", profileService.getAll());

    }

    @GetMapping("/profiles/add")
    public ModelAndView addProfile() {

        return new ModelAndView("addProfile", "profile", new Profile()) ;

    }

    @PostMapping(value = "/profiles/add", consumes = MULTIPART_FORM_DATA_VALUE)
    public String handleProfileAdd(Profile profile, @RequestPart("file") MultipartFile file) {

        log.info("handling request parts: {}", file);

       profileService.save(profile, file);

        return "redirect:/profiles/view";

    }

}
