package com.example.taco_cloud.email;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class EmailOrder {
    private String email;
    private List<String> tacos = new ArrayList<>();
}
