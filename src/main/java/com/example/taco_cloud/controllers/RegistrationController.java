package com.example.taco_cloud.controllers;

import com.example.taco_cloud.form.RegistrationForm;
import com.example.taco_cloud.jdbc.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/register")
public class RegistrationController {

    private UserRepository userRepo;
    private PasswordEncoder passwordEncoder;

    public RegistrationController(
            UserRepository userRepo, PasswordEncoder passwordEncoder
    ){
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String registerForm(){
        return "registrationForm";
    }

    @PostMapping
    public String processRegistration(RegistrationForm form){
        userRepo.save(form.toUser(passwordEncoder));
        return "redirect:/login";
    }
}
