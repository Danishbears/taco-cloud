package com.example.taco_cloud.emulator;


import com.example.taco_cloud.data.Notification;
import com.example.taco_cloud.data.TacoOrder;
import com.example.taco_cloud.repositories.NotificationRepository;
import com.example.taco_cloud.repositories.OrderRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderStatusScheduler {

    private final OrderRepository orderRepo;
    private final NotificationRepository notificationRepo;

    public OrderStatusScheduler(OrderRepository orderRepo,NotificationRepository notificationRepo){
        this.orderRepo = orderRepo;
        this.notificationRepo = notificationRepo;
    }

    @Scheduled(fixedRate = 30000)
    public void processOrderStatusUpdates(){

        List<TacoOrder> activeOrders = orderRepo.findByStatusNot(TacoOrder.Status.COMPLETED);

        for(TacoOrder order : activeOrders){
            switch (order.getStatus()) {
                case CREATED:
                    order.setStatus(TacoOrder.Status.COOKING);
                    orderRepo.save(order);
                    notificationRepo.save(new Notification(
                            order.getUser(),
                            "Order " + order.getId() + " already cooking at the kitchen 🌮"
                    ));
                    break;

                case COOKING:
                    order.setStatus(TacoOrder.Status.DELIVERING);
                    orderRepo.save(order);
                    notificationRepo.save(new Notification(
                            order.getUser(),
                            "Order " + order.getId() + " on its way to you 🚴"
                    ));
                    break;

                case DELIVERING:
                    order.setStatus(TacoOrder.Status.COMPLETED);
                    orderRepo.save(order);
                    notificationRepo.save(new Notification(
                            order.getUser(),
                            "Order " + order.getId() + " delivery completed! Bon appetit! 🎉"
                    ));
                    break;

                default:
                    break;
            }
        }
    }
}
