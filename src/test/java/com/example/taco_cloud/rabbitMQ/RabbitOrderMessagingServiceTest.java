package com.example.taco_cloud.rabbitMQ;

import com.example.taco_cloud.data.TacoOrder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RabbitOrderMessagingServiceTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private RabbitOrderMessagingService messagingService;

    @Test
    void sendOrderPublishesMessageWithWebSourceHeader() {
        TacoOrder order = new TacoOrder();
        order.setDeliveryName("Test Customer");

        MessageConverter converter = mock(MessageConverter.class);
        Message message = mock(Message.class);
        when(rabbitTemplate.getMessageConverter()).thenReturn(converter);
        when(converter.toMessage(eq(order), any(MessageProperties.class))).thenReturn(message);

        messagingService.sendOrder(order);

        verify(rabbitTemplate).send("tacocloud.order", message);
    }
}
