package com.example.taco_cloud.email;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.MessageHeaders;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderSubmitMessageHandlerTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private OrderSubmitMessageHandler handler;

    @Test
    void postsEmailOrderToRestApi() {
        EmailOrder order = new EmailOrder();
        order.setEmail("customer@example.com");
        order.getTacos().add("FLTO: Flour Tortilla");

        when(restTemplate.postForObject(
                eq("http://localhost:8080/api/orders"),
                eq(order),
                eq(String.class)
        )).thenReturn("OK");

        handler.handle(order, new MessageHeaders(null));

        verify(restTemplate).postForObject(
                "http://localhost:8080/api/orders",
                order,
                String.class
        );
    }
}
