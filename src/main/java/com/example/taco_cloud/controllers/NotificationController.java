package com.example.taco_cloud.controllers;


import com.example.taco_cloud.data.Notification;
import com.example.taco_cloud.data.User;
import com.example.taco_cloud.repositories.NotificationRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/notification")
public class NotificationController {

    private final NotificationRepository notificationRepo;

    public NotificationController(NotificationRepository notificationRepo){
        this.notificationRepo = notificationRepo;
    }

    @GetMapping
    public String showNotifications(@AuthenticationPrincipal User user, Model model){
        List<Notification> notifications = notificationRepo.findByUserOrderByCreatedAtDesc(user);

        for(Notification n: notifications){
            if(!n.isRead()){
                n.setRead(true);
                notificationRepo.save(n);
            }
        }
        model.addAttribute("notification",notifications);
        return "notification";
    }

}
