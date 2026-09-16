package com.example.taco_cloud.JSM;

import com.example.taco_cloud.data.TacoOrder;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
@Profile("jms")
public class JmsOrderMessagingService implements OrderMessagingService {

    private final JmsTemplate jms;

    // В Spring Boot 3+ над единственным конструктором @Autowired не нужен
    public JmsOrderMessagingService(JmsTemplate jms) {
        this.jms = jms;
    }

    @Override
    public void sendOrder(TacoOrder order) {
        // Передаем название очереди строкой первым аргументом
        jms.send("tacocloud.order.queue", session -> session.createObjectMessage(order));
    }
}