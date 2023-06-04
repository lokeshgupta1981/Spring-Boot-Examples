package com.howtodoinjava.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {

    @GetMapping("/mustache")
    public String home(Model model) {
        model.addAttribute("message", "Welcome to the Mustache Demo!");
        return "home";
    }

    @GetMapping("/thyme")
    public String homeThymeleaf(Model model) {
        model.addAttribute("message", "Welcome to the Demo using Thymeleaf!");
        return "thymeleaf";
    }

    @GetMapping("/free")
    public String homeFreemarker(Model model) {
        model.addAttribute("message", "Welcome to the Demo using FreeMarker!");
        return "freemarker";
    }


    @GetMapping("/groovy")
    public String template(Model model) {
        model.addAttribute("message", "Welcome to the Demo using Groovy");
        return "index";
    }


    @GetMapping("/jsp")
    public String templateJSP(Model model) {
        model.addAttribute("message", "Welcome to the Demo using JSP");
        return "test";
    }
}

