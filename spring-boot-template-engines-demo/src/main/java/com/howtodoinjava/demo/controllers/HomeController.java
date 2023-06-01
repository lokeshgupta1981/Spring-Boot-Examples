package com.howtodoinjava.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
        model.addAttribute("message", "Welcome to the Demo using Thymleaf!");
        return "thymeleafdemo";
    }

    @GetMapping("/free")
    public String homeFreemarker(Model model) {
        model.addAttribute("message", "Welcome to the Demo using FreeMarker!");
        return "freemarker";
    }


    @GetMapping("/groovy")
    public String template(Model model) {
        model.addAttribute("message", "This is the Groovy template");
        return "test";
    }

    @GetMapping("/jsp")
    public ModelAndView templateJSP(Model model) {
        ModelAndView mav = new ModelAndView("template");

        mav.addObject("message", "This is the JSP template");
        return mav;
//        model.addAttribute("message", "This is the JSP template");
//        return "template";
    }


}