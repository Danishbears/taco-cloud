package com.example.taco_cloud.mappers;

import com.example.taco_cloud.data.Ingredient;
import com.example.taco_cloud.jdbc.IngredientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IngredientByIdMapperTest {

    @Mock
    private IngredientRepository ingredientRepository;

    @InjectMocks
    private IngredientByIdMapper mapper;

    @Test
    void convertReturnsIngredientWhenFound() {
        Ingredient ingredient = new Ingredient("FLTO", "Flour Tortilla", Ingredient.Type.WRAP);
        when(ingredientRepository.findById("FLTO")).thenReturn(Optional.of(ingredient));

        Ingredient result = mapper.convert("FLTO");

        assertThat(result).isEqualTo(ingredient);
    }

    @Test
    void convertReturnsNullWhenNotFound() {
        when(ingredientRepository.findById("UNKNOWN")).thenReturn(Optional.empty());

        Ingredient result = mapper.convert("UNKNOWN");

        assertThat(result).isNull();
    }
}
