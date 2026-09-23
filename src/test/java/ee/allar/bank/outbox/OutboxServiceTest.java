package ee.allar.bank.outbox;

import ee.allar.bank.account.Account;
import ee.allar.bank.account.Balance;
import ee.allar.bank.outbox.mapper.OutboxEventMapper;
import ee.allar.bank.transaction.Transaction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OutboxServiceTest {

    @Mock
    private OutboxEventMapper outboxEventMapper;

    @Mock
    private ObjectMapper objectMapper;

    @Captor
    private ArgumentCaptor<OutboxEvent> eventCapture;

    @InjectMocks
    private OutboxService outboxService;

    @Test
    void recordAccountCreated() {
        Account account = Account.builder().id(UUID.randomUUID()).build();

        outboxService.recordAccountCreated(account);

        verify(outboxEventMapper, times(1)).insert(eventCapture.capture());
        OutboxEvent event = eventCapture.getValue();
        assertThat(event).isNotNull();
        assertAll(
                () -> assertThat(event.getAggregateId()).isEqualTo(account.getId()),
                () -> assertThat(event.getAggregateType()).isEqualTo("ACCOUNT"),
                () -> assertThat(event.getEventType()).isEqualTo(OutboxEventType.CREATED),
                () -> assertThat(event.getPayload()).isEqualTo(toJson(account)),
                () -> assertThat(event.getStatus()).isEqualTo(OutboxEventStatus.PENDING)
        );

    }

    @Test
    void recordBalanceCreated() {
        Balance balance = Balance.builder().id(UUID.randomUUID()).build();

        outboxService.recordBalanceCreated(balance);

        verify(outboxEventMapper, times(1)).insert(eventCapture.capture());
        OutboxEvent event = eventCapture.getValue();
        assertThat(event).isNotNull();
        assertAll(
                () -> assertThat(event.getAggregateId()).isEqualTo(balance.getId()),
                () -> assertThat(event.getAggregateType()).isEqualTo("BALANCE"),
                () -> assertThat(event.getEventType()).isEqualTo(OutboxEventType.CREATED),
                () -> assertThat(event.getPayload()).isEqualTo(toJson(balance)),
                () -> assertThat(event.getStatus()).isEqualTo(OutboxEventStatus.PENDING)
        );
    }

    @Test
    void recordBalanceUpdated() {
        Balance balance = Balance.builder().id(UUID.randomUUID()).build();

        outboxService.recordBalanceUpdated(balance);

        verify(outboxEventMapper, times(1)).insert(eventCapture.capture());
        OutboxEvent event = eventCapture.getValue();
        assertThat(event).isNotNull();
        assertAll(
                () -> assertThat(event.getAggregateId()).isEqualTo(balance.getId()),
                () -> assertThat(event.getAggregateType()).isEqualTo("BALANCE"),
                () -> assertThat(event.getEventType()).isEqualTo(OutboxEventType.UPDATED),
                () -> assertThat(event.getPayload()).isEqualTo(toJson(balance)),
                () -> assertThat(event.getStatus()).isEqualTo(OutboxEventStatus.PENDING)
        );
    }

    @Test
    void recordTransactionCreated() {
        Transaction transaction = Transaction.builder().id(UUID.randomUUID()).build();

        outboxService.recordTransactionCreated(transaction);

        verify(outboxEventMapper, times(1)).insert(eventCapture.capture());
        OutboxEvent event = eventCapture.getValue();
        assertThat(event).isNotNull();
        assertAll(
                () -> assertThat(event.getAggregateId()).isEqualTo(transaction.getId()),
                () -> assertThat(event.getAggregateType()).isEqualTo("TRANSACTION"),
                () -> assertThat(event.getEventType()).isEqualTo(OutboxEventType.CREATED),
                () -> assertThat(event.getPayload()).isEqualTo(toJson(transaction)),
                () -> assertThat(event.getStatus()).isEqualTo(OutboxEventStatus.PENDING)
        );
    }

    @Test
    void findPendingForUpdate() {
        outboxService.findPendingForUpdate(50, 5);
        verify(outboxEventMapper, times(1)).findPendingForUpdate(50, 5);
    }

    @Test
    void markPublished() {
        UUID id = UUID.randomUUID();
        outboxService.markPublished(id);
        verify(outboxEventMapper, times(1)).markPublished(id);
    }

    @Test
    void updateRetryState() {
        UUID id = UUID.randomUUID();
        java.time.Instant nextRetryAt = java.time.Instant.now();
        outboxService.updateRetryState(id, 2, nextRetryAt, "error");
        verify(outboxEventMapper, times(1)).updateRetryState(id, 2, nextRetryAt, "error");
    }

    @Test
    void markFailed() {
        UUID id = UUID.randomUUID();
        outboxService.markFailed(id, "error");
        verify(outboxEventMapper, times(1)).markFailed(id, "error");
    }

    private String toJson(Object payload) {
        return objectMapper.writeValueAsString(payload);
    }
}