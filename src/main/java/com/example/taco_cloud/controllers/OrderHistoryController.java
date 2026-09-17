package com.example.taco_cloud.controllers;


import com.example.taco_cloud.data.TacoOrder;
import com.example.taco_cloud.data.User;
import com.example.taco_cloud.repositories.OrderRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderHistoryController {

    private final OrderRepository orderRepository;

    public OrderHistoryController(OrderRepository orderRepository){
        this.orderRepository = orderRepository;
    }

    @GetMapping("/history")
    public String ordersForUser(@AuthenticationPrincipal User user, Model model ){

        Pageable pageable = PageRequest.of(0,5);
        List<TacoOrder> orders = orderRepository.findByUserOrderByPlacedAtDesc(user,pageable);

        model.addAttribute("orders",orders);

        return "orderHistory";
    }
}
