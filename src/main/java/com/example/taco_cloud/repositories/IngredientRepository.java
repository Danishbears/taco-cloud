package com.example.taco_cloud.repositories;

import com.example.taco_cloud.data.Ingredient;
import org.springframework.data.repository.CrudRepository;

public interface IngredientRepository extends CrudRepository<Ingredient,String>{

}
