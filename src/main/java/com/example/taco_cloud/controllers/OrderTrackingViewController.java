package com.example.taco_cloud.controllers;

import com.example.taco_cloud.data.TacoOrder;
import com.example.taco_cloud.repositories.OrderRepository;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Controller
@RequestMapping("/orders")
public class OrderTrackingViewController {

    private final OrderRepository orderRepo;

    public OrderTrackingViewController(OrderRepository orderRepo) {
        this.orderRepo = orderRepo;
    }

    @GetMapping("/{id}/track")
    public String trackOrderPage(@PathVariable("id") Long orderId, Model model) {
        TacoOrder order = orderRepo.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid order Id:" + orderId));

        model.addAttribute("order", order);
        return "track";
    }
}