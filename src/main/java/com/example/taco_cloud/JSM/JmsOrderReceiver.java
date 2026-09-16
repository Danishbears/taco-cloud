package com.example.taco_cloud.JSM;

import com.example.taco_cloud.data.TacoOrder;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.stereotype.Component;

@Component
public class JmsOrderReceiver implements OrderReceiver {

    private final JmsTemplate jms;
    private final MessageConverter converter;

    public JmsOrderReceiver(JmsTemplate jms, MessageConverter converter) {
        this.jms = jms;
        this.converter = converter;
    }

    @Override
    public TacoOrder receiveOrder() {
        Message message = jms.receive("tacocloud.order.queue");

        if (message != null) {
            try {
                return (TacoOrder) converter.fromMessage(message);
            } catch (JMSException e) {
                throw new RuntimeException("JMS error", e);
            }
        }
        return null;
    }
}