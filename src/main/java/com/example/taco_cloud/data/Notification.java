package com.example.taco_cloud.data;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private User user;

    private String message;
    private LocalDateTime createdAt = LocalDateTime.now();
    private boolean read = false;

    public Notification(User user,String message){
        this.user = user;
        this.message = message;
    }
}
