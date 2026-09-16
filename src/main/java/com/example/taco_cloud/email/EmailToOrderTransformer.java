package com.example.taco_cloud.email;


import com.example.taco_cloud.data.Ingredient;
import jakarta.mail.Address;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.mail.transformer.AbstractMailMessageTransformer;
import org.springframework.integration.support.AbstractIntegrationMessageBuilder;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class EmailToOrderTransformer
        extends AbstractMailMessageTransformer<EmailOrder> {

    private static final Logger log = LoggerFactory.getLogger(EmailToOrderTransformer.class);
    private static final String SUBJECT_KEYWORDS = "TACO ORDER";

    @Override
    protected AbstractIntegrationMessageBuilder<EmailOrder> doTransform(Message mailMessage) {
        EmailOrder emailOrder = processPayload(mailMessage);

        if (emailOrder == null) {
            return null; // ignore if piercing error
        }

        return MessageBuilder.withPayload(emailOrder);
    }

    private EmailOrder processPayload(Message mailMessage) {
        try {
            String subject = mailMessage.getSubject();
            if (subject != null && subject.toUpperCase().contains(SUBJECT_KEYWORDS)) {
                Address[] from = mailMessage.getFrom();
                if (from != null && from.length > 0) {
                    String email = ((InternetAddress) from[0]).getAddress();
                    String content = mailMessage.getContent().toString();
                    return parseEmailToOrder(email, content);
                }
            }
        } catch (MessagingException | IOException e) {
            log.error("Error with receiving message", e);
        }
        return null;
    }

    private EmailOrder parseEmailToOrder(String email, String content) {
        EmailOrder emailOrder = new EmailOrder();
        emailOrder.setEmail(email);

        String[] lines = content.split("\\r?\\n");
        for (String line : lines) {
            if (line.contains(":")) {
                emailOrder.getTacos().add(line.trim());
            }
        }

        return emailOrder;
    }
//
//    private static Ingredient[] ALL_INGREDIENTS = new Ingredient[] {
//            new Ingredient("FLTO", "FLOUR TORTILLA"),
//            new Ingredient("COTO", "CORN TORTILLA"),
//            new Ingredient("GRBF", "GROUND BEEF"),
//            new Ingredient("CARN", "CARNITAS"),
//            new Ingredient("TMTO", "TOMATOES"),
//            new Ingredient("LETC", "LETTUCE"),
//            new Ingredient("CHED", "CHEDDAR"),
//            new Ingredient("JACK", "MONTERREY JACK"),
//            new Ingredient("SLSA", "SALSA"),
//            new Ingredient("SRCR", "SOUR CREAM")
//    };
}