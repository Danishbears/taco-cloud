package com.example.taco_cloud.repositories;

import com.example.taco_cloud.data.Notification;
import com.example.taco_cloud.data.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface NotificationRepository extends CrudRepository<Notification,Long> {
    List<Notification> findByUserOrderByCreatedAtDesc(User user);

    long countByUserAndReadFalse(User user);
}
