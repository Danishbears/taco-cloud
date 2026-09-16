package com.example.taco_cloud.controllers;


import com.example.taco_cloud.data.TacoOrder;
import com.example.taco_cloud.data.User;
import com.example.taco_cloud.jdbc.OrderRepository;
import com.example.taco_cloud.jdbc.UserRepository;
import com.example.taco_cloud.JSM.OrderMessagingService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import java.awt.print.Pageable;

@Slf4j
@Controller
@RequestMapping("/orders")
@SessionAttributes("order")
@ConfigurationProperties(prefix = "taco.orders")
public class OrderController {

    private int pageSize = 20;
    private OrderRepository orderRepo;
    private OrderMessagingService messagingService;
    private UserRepository userRepo;

    public OrderController(OrderRepository orderRepo, UserRepository userRepo,OrderMessagingService orderMessagingService){

        this.messagingService = orderMessagingService;
        this.orderRepo = orderRepo;
        this.userRepo = userRepo;
    }

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public TacoOrder postOrder(@RequestBody TacoOrder order) {
        messagingService.sendOrder(order);
        return orderRepo.save(order);
    }

    @GetMapping("/current")
    public String orderForm(){
        return "orderForm";
    }

    @GetMapping
    public String ordersForUser(
            @AuthenticationPrincipal User user, Model model
    ){
        Pageable pageable = (Pageable) PageRequest.of(0,pageSize);
        model.addAttribute("orders",
                orderRepo.findByUserOrderByPlacedAtDesc(user,pageable));
        return "orderList";

    }

    @PostMapping
    public String processOrder(@Valid TacoOrder order, Errors errors, SessionStatus sessionStatus, @AuthenticationPrincipal User user){

        if(errors.hasErrors()){
            return "orderForm";
        }

        order.setUser(user);
        orderRepo.save(order);
        //log.info("Order submitted: {}",order);
        sessionStatus.setComplete(); // we are ensuring that the session is cleaned up and ready for a new order the next time the user creates a taco.
        return "redirect:/";
    }

    @PutMapping(path = "/{orderId}",consumes = "application/json")
    public TacoOrder putOrder(
            @PathVariable("orderId") Long orderId,
            @RequestBody TacoOrder order
    ){
        order.setId(orderId);
        return orderRepo.save(order);
    }


    @PatchMapping(path="/{orderId}", consumes="application/json")
    public TacoOrder patchOrder(@PathVariable("orderId") Long orderId,
                                @RequestBody TacoOrder patch) {
        TacoOrder order = orderRepo.findById(orderId).get();
        if (patch.getDeliveryName() != null) {
            order.setDeliveryName(patch.getDeliveryName());
        }
        if (patch.getDeliveryStreet() != null) {
            order.setDeliveryStreet(patch.getDeliveryStreet());
        }
        if (patch.getDeliveryCity() != null) {
            order.setDeliveryCity(patch.getDeliveryCity());
        }
        if (patch.getDeliveryState() != null) {
            order.setDeliveryState(patch.getDeliveryState());
        }
        if (patch.getDeliveryZip() != null) {
            order.setDeliveryZip(patch.getDeliveryZip());
        }
        if (patch.getCcNumber() != null) {
            order.setCcNumber(patch.getCcNumber());
        }
        if (patch.getCcExpiration() != null) {
            order.setCcExpiration(patch.getCcExpiration());
        }
        if (patch.getCcCVV() != null) {
            order.setCcCVV(patch.getCcCVV());
        }
        return orderRepo.save(order);
    }

    @DeleteMapping("/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable("orderId") Long orderId){
        try {
            orderRepo.deleteById(orderId);
        }catch(EmptyResultDataAccessException e){}
    }

}
