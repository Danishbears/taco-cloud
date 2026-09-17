package com.example.taco_cloud.controllers;

import com.example.taco_cloud.JSM.OrderMessagingService;
import com.example.taco_cloud.data.TacoOrder;
import com.example.taco_cloud.data.User;
import com.example.taco_cloud.repositories.OrderRepository;
import com.example.taco_cloud.repositories.UserRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable; // ИСПРАВЛЕН ИМПОРТ!
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

@Slf4j
@Controller
@RequestMapping("/orders")
@SessionAttributes("tacoOrder")
public class OrderController {

    @Value("${taco.orders.page-size:20}")
    private int pageSize;

    private final OrderRepository orderRepo;
    private final OrderMessagingService messagingService;
    private final UserRepository userRepo;

    public OrderController(OrderRepository orderRepo, UserRepository userRepo, OrderMessagingService orderMessagingService) {
        this.messagingService = orderMessagingService;
        this.orderRepo = orderRepo;
        this.userRepo = userRepo;
    }



    @GetMapping("/current")
    public String orderForm(@AuthenticationPrincipal User user, @ModelAttribute  TacoOrder tacoOrder) {

        if (tacoOrder.getDeliveryName() == null) {
            tacoOrder.setDeliveryName(user.getFullname());
        }
        if (tacoOrder.getDeliveryStreet() == null) {
            tacoOrder.setDeliveryStreet(user.getStreet());
        }
        if (tacoOrder.getDeliveryCity() == null) {
            tacoOrder.setDeliveryCity(user.getCity());
        }
        if (tacoOrder.getDeliveryState() == null) {
            tacoOrder.setDeliveryState(user.getState());
        }
        if (tacoOrder.getDeliveryZip() == null) {
            tacoOrder.setDeliveryZip(user.getZip());
        }

        return "orderForm";
    }

    @GetMapping
    public String ordersForUser(@AuthenticationPrincipal User user, Model model) {
        Pageable pageable = PageRequest.of(0, pageSize);
        model.addAttribute("orders", orderRepo.findByUserOrderByPlacedAtDesc(user, pageable));
        return "orderForm";
    }

    @PostMapping
    public String processOrder(@Valid TacoOrder order, Errors errors, SessionStatus sessionStatus, @AuthenticationPrincipal User user) {
        if (errors.hasErrors()) {
            return "orderForm";
        }

        order.setUser(user);
        orderRepo.save(order);
        sessionStatus.setComplete();
        return "redirect:/";
    }

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public TacoOrder postOrder(@RequestBody TacoOrder order) {
        messagingService.sendOrder(order);
        return orderRepo.save(order);
    }

    @PutMapping(path = "/{orderId}", consumes = "application/json")
    @ResponseBody
    public TacoOrder putOrder(@PathVariable("orderId") Long orderId, @RequestBody TacoOrder order) {
        order.setId(orderId);
        return orderRepo.save(order);
    }

    @PatchMapping(path="/{orderId}", consumes="application/json")
    @ResponseBody
    public TacoOrder patchOrder(@PathVariable("orderId") Long orderId, @RequestBody TacoOrder patch) {
        TacoOrder order = orderRepo.findById(orderId).orElseThrow();
        if (patch.getDeliveryName() != null) order.setDeliveryName(patch.getDeliveryName());
        if (patch.getDeliveryStreet() != null) order.setDeliveryStreet(patch.getDeliveryStreet());
        if (patch.getDeliveryCity() != null) order.setDeliveryCity(patch.getDeliveryCity());
        if (patch.getDeliveryState() != null) order.setDeliveryState(patch.getDeliveryState());
        if (patch.getDeliveryZip() != null) order.setDeliveryZip(patch.getDeliveryZip());
        if (patch.getCcNumber() != null) order.setCcNumber(patch.getCcNumber());
        if (patch.getCcExpiration() != null) order.setCcExpiration(patch.getCcExpiration());
        if (patch.getCcCVV() != null) order.setCcCVV(patch.getCcCVV());
        return orderRepo.save(order);
    }

    @DeleteMapping("/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable("orderId") Long orderId) {
        try {
            orderRepo.deleteById(orderId);
        } catch (EmptyResultDataAccessException e) {
            log.warn("Attempt to delete non-existing order: {}", orderId);
        }
    }
}