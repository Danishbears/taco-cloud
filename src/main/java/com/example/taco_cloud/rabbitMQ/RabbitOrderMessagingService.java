package com.example.taco_cloud.rabbitMQ;


import com.example.taco_cloud.JSM.OrderMessagingService;
import com.example.taco_cloud.data.TacoOrder;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile({"rabbitmq", "default"})
public class RabbitOrderMessagingService implements OrderMessagingService {

    private RabbitTemplate rabbitTemplate;

    @Autowired
    public RabbitOrderMessagingService(RabbitTemplate rabbitTemplate){
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void sendOrder(TacoOrder order) {

       // rabbitTemplate.convertAndSend("tacocloud.order", order);

        MessageConverter converter = rabbitTemplate.getMessageConverter();
        MessageProperties props = new MessageProperties();
        props.setHeader("X_ORDER_SOURCE","WEB");
        Message message = converter.toMessage(order,props);
        rabbitTemplate.send("tacocloud.order",message);

    }
}
