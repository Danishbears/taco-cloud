package com.example.taco_cloud.rabbitMQ;


import com.example.taco_cloud.data.TacoOrder;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitOrderReceiver {

    private RabbitTemplate rabbitTemplate;
    private MessageConverter converter;

    @Autowired
    public RabbitOrderReceiver(RabbitTemplate rabbitTemplate){
        this.rabbitTemplate = rabbitTemplate;
        this.converter = rabbitTemplate.getMessageConverter();
    }

    public TacoOrder receiveOrder(){
//        Message message = rabbitTemplate.receive("tacocloud.order",3000);
//        return message != null ? (TacoOrder) converter.fromMessage(message) : null;
        return (TacoOrder) rabbitTemplate.receiveAndConvert("tacocloud.order.queue");
    }

}
