package com.example.taco_cloud.service;

import com.example.taco_cloud.data.Ingredient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestIngredientServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private RestIngredientService service;

    @BeforeEach
    void setUp() {
        service = new RestIngredientService(restTemplate);
    }

    @Test
    void findAllReturnsIngredientsFromRestApi() {
        Ingredient[] ingredients = {
                new Ingredient("FLTO", "Flour Tortilla", Ingredient.Type.WRAP),
                new Ingredient("GRBF", "Ground Beef", Ingredient.Type.PROTEIN)
        };
        when(restTemplate.getForObject(
                "http://localhost:8080/api/ingredients",
                Ingredient[].class
        )).thenReturn(ingredients);

        Iterable<Ingredient> result = service.findAll();

        assertThat(result).containsExactly(ingredients);
    }

    @Test
    void findAllReturnsEmptyListWhenApiReturnsNull() {
        when(restTemplate.getForObject(
                "http://localhost:8080/api/ingredients",
                Ingredient[].class
        )).thenReturn(null);

        Iterable<Ingredient> result = service.findAll();

        assertThat(result).isEmpty();
    }

    @Test
    void addIngredientPostsToRestApi() {
        Ingredient ingredient = new Ingredient("FLTO", "Flour Tortilla", Ingredient.Type.WRAP);
        when(restTemplate.postForObject(
                eq("http://localhost:8080/api/ingredients"),
                eq(ingredient),
                eq(Ingredient.class)
        )).thenReturn(ingredient);

        Ingredient saved = service.addIngredient(ingredient);

        assertThat(saved).isEqualTo(ingredient);
        verify(restTemplate).postForObject(
                "http://localhost:8080/api/ingredients",
                ingredient,
                Ingredient.class
        );
    }
}
