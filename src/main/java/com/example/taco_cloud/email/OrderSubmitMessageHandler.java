package com.example.taco_cloud.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.core.GenericHandler;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class OrderSubmitMessageHandler implements GenericHandler<EmailOrder> {

    private static final Logger log = LoggerFactory.getLogger(OrderSubmitMessageHandler.class);

    private final RestTemplate restTemplate;

    public OrderSubmitMessageHandler(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Object handle(EmailOrder payload, MessageHeaders headers) {
        log.info("Received completed order from:  {}", payload.getEmail());

        try {
            restTemplate.postForObject("http://localhost:8080/api/orders", payload, String.class);
            log.info("Order from {} successfully has been sent to  REST API", payload.getEmail());
        } catch (Exception e) {
            log.error("Error REST API", e);
        }

        return null;
    }
}