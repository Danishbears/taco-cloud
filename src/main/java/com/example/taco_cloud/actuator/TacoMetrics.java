package com.example.taco_cloud.actuator;

import java.util.List;

import com.example.taco_cloud.data.Ingredient;
import com.example.taco_cloud.data.Taco;
import org.springframework.data.rest.core.event.AbstractRepositoryEventListener;
import org.springframework.stereotype.Component;
import io.micrometer.core.instrument.MeterRegistry;

@Component
public class TacoMetrics extends AbstractRepositoryEventListener<Taco> {

    private final MeterRegistry meterRegistry;

    public TacoMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    protected void onAfterCreate(Taco taco) {
        List<Ingredient> ingredients = taco.getIngredients();
        if (ingredients != null) {
            for (Ingredient ingredient : ingredients) {
                meterRegistry.counter("tacocloud", "ingredient", ingredient.getId()).increment();
            }
        }
    }
}