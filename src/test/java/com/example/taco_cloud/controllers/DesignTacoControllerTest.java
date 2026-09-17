package com.example.taco_cloud.controllers;

import com.example.taco_cloud.data.Ingredient;
import com.example.taco_cloud.repositories.IngredientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.taco_cloud.support.ControllerTestSupport;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class DesignTacoControllerTest {

    @Mock
    private IngredientRepository ingredientRepository;

    @InjectMocks
    private DesignTacoController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = ControllerTestSupport.standaloneSetup(controller);
    }

    @Test
    void showDesignFormDisplaysIngredientsGroupedByType() throws Exception {
        when(ingredientRepository.findAll()).thenReturn(List.of(
                new Ingredient("FLTO", "Flour Tortilla", Ingredient.Type.WRAP),
                new Ingredient("GRBF", "Ground Beef", Ingredient.Type.PROTEIN),
                new Ingredient("CHED", "Cheddar", Ingredient.Type.CHEESE)
        ));

        mockMvc.perform(get("/design"))
                .andExpect(status().isOk())
                .andExpect(view().name("design"))
                .andExpect(model().attribute("wrap", hasSize(1)))
                .andExpect(model().attribute("protein", hasSize(1)))
                .andExpect(model().attribute("cheese", hasSize(1)));
    }

    @Test
    void processTacoWithValidationErrorsReturnsToDesignForm() throws Exception {
        when(ingredientRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(post("/design")
                        .param("name", "bad"))
                .andExpect(status().isOk())
                .andExpect(view().name("design"));
    }
}
