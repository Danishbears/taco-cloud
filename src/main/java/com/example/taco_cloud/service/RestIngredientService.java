package com.example.taco_cloud.service;

import com.example.taco_cloud.data.Ingredient;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

public class RestIngredientService implements IngredientService {

    private final RestTemplate restTemplate;

    public RestIngredientService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Iterable<Ingredient> findAll() {
        Ingredient[] ingredients = restTemplate.getForObject(
                "http://localhost:8080/api/ingredients",
                Ingredient[].class
        );
        return ingredients != null ? Arrays.asList(ingredients) : List.of();
    }

    @Override
    public Ingredient addIngredient(Ingredient ingredient) {
        return restTemplate.postForObject(
                "http://localhost:8080/api/ingredients",
                ingredient,
                Ingredient.class
        );
    }
}