package com.example.taco_cloud.JSM;

import com.example.taco_cloud.data.TacoOrder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class JmsOrderMessagingServiceTest {

    @Mock
    private JmsTemplate jmsTemplate;

    @InjectMocks
    private JmsOrderMessagingService messagingService;

    @Test
    void sendOrderDispatchesToTacoCloudQueue() {
        TacoOrder order = new TacoOrder();
        order.setDeliveryName("Test Customer");

        messagingService.sendOrder(order);

        ArgumentCaptor<MessageCreator> creatorCaptor = ArgumentCaptor.forClass(MessageCreator.class);
        verify(jmsTemplate).send(eq("tacocloud.order.queue"), creatorCaptor.capture());
    }
}
