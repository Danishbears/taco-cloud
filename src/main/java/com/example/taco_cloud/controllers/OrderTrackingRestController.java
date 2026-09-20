package com.example.taco_cloud.controllers;



import com.example.taco_cloud.data.TacoOrder;
import com.example.taco_cloud.repositories.OrderRepository;
import com.example.taco_cloud.service.OrderNotificationService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/orders")
public class OrderTrackingRestController {

    private final OrderRepository orderRepo;
    private final OrderNotificationService notificationService;

    public OrderTrackingRestController(OrderRepository orderRepo, OrderNotificationService notificationService) {
        this.orderRepo = orderRepo;
        this.notificationService = notificationService;
    }

    @GetMapping(value = "/{orderId}/track", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter trackOrder(@PathVariable("orderId") Long orderId) {
        SseEmitter emitter = new SseEmitter(0L);

        TacoOrder order = orderRepo.findById(orderId).orElse(null);

        if (order == null) {
            emitter.complete();
            return emitter;
        }

        if (TacoOrder.Status.COMPLETED.equals(order.getStatus())) {
            try {
                emitter.send(SseEmitter.event().data(Map.of(
                        "status", TacoOrder.Status.COMPLETED.name(),
                        "progress", 1.0
                )));
                emitter.complete();
            } catch (IOException e) {
                emitter.completeWithError(e);
            }
            return emitter;
        }

        double initialProgress = 0.0;
        if (TacoOrder.Status.COOKING.equals(order.getStatus())) {
            initialProgress = 0.3;
        } else if (TacoOrder.Status.DELIVERING.equals(order.getStatus())) {
            initialProgress = 0.6;
        }

        final double startProgress = initialProgress;

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(new Runnable() {
            private double progress = 0.0;

            @Override
            public void run() {
                try {
                    TacoOrder.Status currentStatus;

                    // Multi-stage status evaluation based on progress
                    if (progress == 0.0) {
                        currentStatus = TacoOrder.Status.CREATED;
                    } else if (progress < 0.3) {
                        currentStatus = TacoOrder.Status.COOKING;
                    } else if (progress < 1.0) {
                        currentStatus = TacoOrder.Status.DELIVERING;
                    } else {
                        progress = 1.0;
                        currentStatus = TacoOrder.Status.COMPLETED;
                    }

                    // Save updated status to Database
                    order.setStatus(currentStatus);
                    orderRepo.save(order);

                    // Send global application notification
                    notificationService.notifyStatusChange(orderId, currentStatus.name());

                    // Send SSE payload to live tracker map
                    emitter.send(SseEmitter.event().data(Map.of(
                            "status", currentStatus.name(),
                            "progress", progress
                    )));

                    if (progress >= 1.0) {
                        emitter.complete();
                        executor.shutdown();
                    }

                    // Increment progress by 10% per tick (~30s delivery run)
                    progress += 0.1;

                } catch (Exception e) {
                    emitter.completeWithError(e);
                    executor.shutdown();
                }
            }
        }, 0, 3, TimeUnit.SECONDS);

        return emitter;
    }
}