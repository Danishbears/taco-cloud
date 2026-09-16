package com.example.taco_cloud.controllers;


import com.example.taco_cloud.data.Ingredient;
import com.example.taco_cloud.jdbc.IngredientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path="/api/ingredients", produces="application/json")
@CrossOrigin(origins = "http://localhost:8080")
public class IngredientController {

    private IngredientRepository repo;

    @Autowired
    public IngredientController(IngredientRepository repo){
        this.repo = repo;
    }

    @GetMapping
    public Iterable<Ingredient> allIngredients(){
        return repo.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Ingredient saveIngredient(@RequestBody Ingredient ingredient){
        return repo.save(ingredient);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteIngredient(@PathVariable("id") String ingredientId){
        repo.deleteById(ingredientId);
    }
}
