package com.example.taco_cloud.data;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TacoOrderTest {

    @Test
    void addTacoAppendsToOrder() {
        TacoOrder order = new TacoOrder();
        Taco taco = new Taco();
        taco.setName("Test Taco");

        order.addTaco(taco);

        assertThat(order.getTacos()).containsExactly(taco);
        assertThat(order.getTacos()).hasSize(1);
    }
}
