package com.example.taco_cloud.config;

import com.example.taco_cloud.data.TacoOrder;
import jakarta.jms.Destination;
import org.apache.activemq.artemis.jms.client.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import java.util.Collections;

@Configuration
public class MessagingConfig {

    @Bean
    public MessageConverter messageConverter() {
        MappingJackson2MessageConverter messageConverter = new MappingJackson2MessageConverter();
        messageConverter.setTypeIdPropertyName("_typeId");
        messageConverter.setTargetType(MessageType.TEXT);
        messageConverter.setTypeIdMappings(
                Collections.singletonMap("order", TacoOrder.class)
        );
        return messageConverter;
    }

    @Bean
    public Destination orderQueue() {
        return new ActiveMQQueue("tacocloud.order.queue");
    }
}