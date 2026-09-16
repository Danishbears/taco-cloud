package com.example.taco_cloud.email;

import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.integration.support.MessageBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailToOrderTransformerTest {

    @Mock
    private Message mailMessage;

    private EmailToOrderTransformer transformer;

    @BeforeEach
    void setUp() {
        transformer = new EmailToOrderTransformer();
    }

    @Test
    void transformsTacoOrderEmailIntoEmailOrder() throws Exception {
        when(mailMessage.getSubject()).thenReturn("TACO ORDER from hungry customer");
        when(mailMessage.getFrom()).thenReturn(new InternetAddress[]{
                new InternetAddress("hungry@example.com")
        });
        when(mailMessage.getContent()).thenReturn("""
                FLTO: Flour Tortilla
                GRBF: Ground Beef
                CHED: Cheddar
                """);

        var result = transformer.transform(MessageBuilder.withPayload(mailMessage).build());

        assertThat(result).isNotNull();
        EmailOrder order = (EmailOrder) result.getPayload();
        assertThat(order.getEmail()).isEqualTo("hungry@example.com");
        assertThat(order.getTacos()).containsExactly(
                "FLTO: Flour Tortilla",
                "GRBF: Ground Beef",
                "CHED: Cheddar"
        );
    }

}
