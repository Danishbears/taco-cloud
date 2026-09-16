package com.example.taco_cloud.email;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.mail.dsl.Mail;
import org.springframework.web.client.RestTemplate;

@Configuration
public class TacoOrderEmailIntegrationConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public IntegrationFlow tacoOrderEmailFlow(
            EmailProperties emailProps,
            EmailToOrderTransformer emailToOrderTransformer,
            OrderSubmitMessageHandler orderSubmitHandler) {

        // В современных версиях используем IntegrationFlow.from() вместо IntegrationFlows.from()
        return IntegrationFlow
                .from(Mail.imapInboundAdapter(emailProps.getImapUrl()),
                        e -> e.poller(Pollers.fixedDelay(emailProps.getPollRate())))
                .transform(emailToOrderTransformer)
                .handle(orderSubmitHandler)
                .get();
    }
}