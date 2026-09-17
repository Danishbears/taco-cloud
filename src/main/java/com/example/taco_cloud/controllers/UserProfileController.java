package com.example.taco_cloud.controllers;


import com.example.taco_cloud.data.Notification;
import com.example.taco_cloud.data.ProfileForm;
import com.example.taco_cloud.data.TacoOrder;
import com.example.taco_cloud.data.User;
import com.example.taco_cloud.repositories.NotificationRepository;
import com.example.taco_cloud.repositories.OrderRepository;
import com.example.taco_cloud.repositories.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/profile")
public class UserProfileController {
    private final UserRepository userRepo;
    private final OrderRepository orderRepo;
    private final NotificationRepository notificationRepo;

    public UserProfileController(UserRepository userRepo, OrderRepository orderRepo, NotificationRepository notificationRepo){
        this.userRepo = userRepo;
        this.orderRepo = orderRepo;
        this.notificationRepo = notificationRepo;
    }

    @GetMapping
    public String showProfile(@AuthenticationPrincipal User user, Model model) {
        ProfileForm form = new ProfileForm();
        form.setFullname(user.getFullname());
        form.setStreet(user.getStreet());
        form.setCity(user.getCity());
        form.setState(user.getState());
        form.setZip(user.getZip());
        form.setPhoneNumber(user.getPhoneNumber());

        Pageable pageable = PageRequest.of(0,5);
        List<TacoOrder> orders = orderRepo.findByUserOrderByPlacedAtDesc(user,pageable);

        List<Notification> notifications = notificationRepo.findByUserOrderByCreatedAtDesc(user);
        long unreadCount = notificationRepo.countByUserAndReadFalse(user);

        model.addAttribute("user", user);
        model.addAttribute("profileForm", form);
        model.addAttribute("notification", notifications);
        model.addAttribute("orders",orders);
        return "profile";
    }

    @PostMapping
    public String updateProfile(@AuthenticationPrincipal User user, @ModelAttribute("profileForm") ProfileForm form) {
        user.setFullname(form.getFullname());
        user.setStreet(form.getStreet());
        user.setCity(form.getCity());
        user.setState(form.getState());
        user.setZip(form.getZip());
        user.setPhoneNumber(form.getPhoneNumber());

        userRepo.save(user);
        return "redirect:/profile?success";
    }
}
