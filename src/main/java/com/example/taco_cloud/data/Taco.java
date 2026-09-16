package com.example.taco_cloud.data;


import com.example.taco_cloud.data.Ingredient;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.bind.annotation.GetMapping;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@Entity(name="tacos")
public class Taco {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Size(min=5, message="Name must be at least 5 characters long")
    private String name;
    private Date createdAt = new Date();


    @Size(min=1, message="You must choose at least 1 ingredient")
    @Column(name = "ingredients")
    @ManyToMany
    private List<Ingredient> ingredients = new ArrayList<>();

    public void addIngredient(Ingredient ingredient) {
        this.ingredients.add(ingredient);
    }



}
