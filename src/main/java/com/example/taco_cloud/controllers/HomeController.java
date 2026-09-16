package com.example.taco_cloud.controllers;


import com.example.taco_cloud.data.Taco;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("taco", new Taco());
        return "home";
    }
}

