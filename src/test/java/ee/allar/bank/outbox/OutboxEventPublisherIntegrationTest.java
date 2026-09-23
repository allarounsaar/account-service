package ee.allar.bank.outbox;

import ee.allar.bank.BaseIntegrationTest;
import ee.allar.bank.outbox.mapper.OutboxEventMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OutboxEventPublisherIntegrationTest extends BaseIntegrationTest {

    private static final String QUEUE_NAME = "test.account.created.queue";

    @Autowired
    private OutboxPublisherScheduler scheduler;

    @Autowired
    private OutboxEventMapper outboxEventMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitAdmin rabbitAdmin;

    @Autowired
    private TopicExchange bankEventsExchange;

    @BeforeEach
    void setUpQueue() {
        rabbitAdmin.declareExchange(bankEventsExchange);
        Queue testQueue = new Queue(QUEUE_NAME, true, false, false);
        rabbitAdmin.declareQueue(testQueue);
        rabbitAdmin.declareBinding(BindingBuilder.bind(testQueue).to(bankEventsExchange).with("account.created"));
    }

    @Test
    void publish_sendsMessageToRabbitMqExchangeAndMarksPublished() {
        OutboxEvent event = OutboxEvent.builder()
                .aggregateId(UUID.randomUUID())
                .aggregateType("ACCOUNT")
                .eventType(OutboxEventType.CREATED)
                .payload("{\"test\": \"payload\"}")
                .status(OutboxEventStatus.PENDING)
                .build();

        outboxEventMapper.insert(event);

        scheduler.publish();

        String receivedPayload = (String) rabbitTemplate.receiveAndConvert(QUEUE_NAME, 5000);
        assertThat(receivedPayload).isEqualTo(event.getPayload());

        List<Map<String, Object>> events = findOutboxEvents("ACCOUNT", event.getAggregateId());
        assertThat(events).hasSize(1);
        assertThat(events.getFirst().get("status")).isEqualTo("PUBLISHED");
        assertThat(events.getFirst().get("published_at")).isNotNull();
    }
}
