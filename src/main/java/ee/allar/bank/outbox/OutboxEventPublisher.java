package ee.allar.bank.outbox;

import ee.allar.bank.config.RabbitMqConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    void publish(OutboxEvent event) {
        String routingKey = event.getAggregateType().toLowerCase() + "." + event.getEventType().name().toLowerCase();
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.EXCHANGE,
                routingKey,
                event.getPayload()
        );
    }
}
