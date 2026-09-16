package com.example.taco_cloud.JSM;



import com.example.taco_cloud.data.TacoOrder;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class OrderListener {

    // Spring сам перехватит сообщение из очереди,
    // десериализует его в TacoOrder и передаст в этот метод
    @JmsListener(destination = "tacocloud.order.queue")
    public void receiveOrder(TacoOrder order) {
        // Заменяем фейковый KitchenUI на обычный вывод в консоль
        System.out.println("=== ПОЛУЧЕН НОВЫЙ ЗАКАЗ ИЗ JMS ===");
        System.out.println("Имя клиента: " + order.getDeliveryName());
        System.out.println("Адрес: " + order.getDeliveryStreet());
        System.out.println("==================================");
    }
}