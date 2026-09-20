package com.example.taco_cloud.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OrderNotificationService {

    private final Map<Long, SseEmitter> userEmitters = new ConcurrentHashMap<>();

    public void registerGlobalNotificationEmitter(Long userId, SseEmitter emitter) {
        userEmitters.put(userId, emitter);
        emitter.onCompletion(() -> userEmitters.remove(userId));
        emitter.onTimeout(() -> userEmitters.remove(userId));
    }

    public void notifyStatusChange(Long orderId, String newStatus) {
        // Логируем единый статус для отладки
        System.out.println("[TacoCloud Notification] Order #" + orderId + " changed status to: " + newStatus);

        userEmitters.forEach((userId, emitter) -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("order-status-update")
                        .data(Map.of("orderId", orderId, "status", newStatus)));
            } catch (IOException e) {
                userEmitters.remove(userId);
            }
        });
    }
}