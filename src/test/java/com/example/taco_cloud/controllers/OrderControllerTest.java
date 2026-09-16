package com.example.taco_cloud.controllers;

import com.example.taco_cloud.JSM.OrderMessagingService;
import com.example.taco_cloud.data.TacoOrder;
import com.example.taco_cloud.jdbc.OrderRepository;
import com.example.taco_cloud.support.ControllerTestSupport;
import com.example.taco_cloud.jdbc.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderMessagingService messagingService;

    @InjectMocks
    private OrderController controller;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = ControllerTestSupport.standaloneSetup(controller);
        objectMapper = new ObjectMapper();
    }

    @Test
    void postOrderSavesAndSendsMessage() throws Exception {
        TacoOrder order = validOrder();
        when(orderRepository.save(any(TacoOrder.class))).thenAnswer(inv -> inv.getArgument(0));

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isCreated());

        verify(messagingService).sendOrder(any(TacoOrder.class));
        verify(orderRepository).save(any(TacoOrder.class));
    }

    @Test
    void putOrderSetsIdAndSaves() throws Exception {
        TacoOrder order = validOrder();
        when(orderRepository.save(any(TacoOrder.class))).thenAnswer(inv -> inv.getArgument(0));

        mockMvc.perform(put("/orders/42")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isOk());

        verify(orderRepository).save(org.mockito.ArgumentMatchers.argThat(
                saved -> saved.getId().equals(42L)));
    }

    @Test
    void patchOrderUpdatesOnlyProvidedFields() throws Exception {
        TacoOrder existing = validOrder();
        existing.setId(1L);
        existing.setDeliveryCity("Old City");
        when(orderRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(orderRepository.save(any(TacoOrder.class))).thenAnswer(inv -> inv.getArgument(0));

        TacoOrder patch = new TacoOrder();
        patch.setDeliveryCity("New City");

        mockMvc.perform(patch("/orders/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patch)))
                .andExpect(status().isOk());

        verify(orderRepository).save(org.mockito.ArgumentMatchers.argThat(
                saved -> "New City".equals(saved.getDeliveryCity())
                        && "Test User".equals(saved.getDeliveryName())));
    }

    @Test
    void deleteOrderRemovesById() throws Exception {
        mockMvc.perform(delete("/orders/99"))
                .andExpect(status().isNoContent());

        verify(orderRepository).deleteById(99L);
    }

    @Test
    void deleteOrderIgnoresMissingOrder() throws Exception {
        doThrow(new EmptyResultDataAccessException(1))
                .when(orderRepository).deleteById(99L);

        mockMvc.perform(delete("/orders/99"))
                .andExpect(status().isNoContent());
    }

    @Test
    void orderFormReturnsOrderFormView() throws Exception {
        mockMvc.perform(get("/orders/current"))
                .andExpect(status().isOk())
                .andExpect(view().name("orderForm"));
    }

    @Test
    void processOrderSavesOrderAndRedirects() throws Exception {
        when(orderRepository.save(any(TacoOrder.class))).thenAnswer(inv -> inv.getArgument(0));

        mockMvc.perform(post("/orders")
                        .param("deliveryName", "Test User")
                        .param("deliveryStreet", "123 Main St")
                        .param("deliveryCity", "Springfield")
                        .param("deliveryState", "IL")
                        .param("deliveryZip", "62704")
                        .param("ccNumber", "4111111111111111")
                        .param("ccExpiration", "12/28")
                        .param("ccCVV", "123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(orderRepository).save(any(TacoOrder.class));
    }

    private TacoOrder validOrder() {
        TacoOrder order = new TacoOrder();
        order.setDeliveryName("Test User");
        order.setDeliveryStreet("123 Main St");
        order.setDeliveryCity("Springfield");
        order.setDeliveryState("IL");
        order.setDeliveryZip("62704");
        order.setCcNumber("4111111111111111");
        order.setCcExpiration("12/28");
        order.setCcCVV("123");
        return order;
    }
}
