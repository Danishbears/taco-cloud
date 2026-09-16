package com.example.taco_cloud.service;

import com.example.taco_cloud.data.Ingredient;

public interface IngredientService {
    Iterable<Ingredient> findAll();
    Ingredient addIngredient(Ingredient ingredient);
}