package ee.allar.bank.outbox;

import ee.allar.bank.account.Account;
import ee.allar.bank.account.Balance;
import ee.allar.bank.outbox.dto.AccountCreatedEvent;
import ee.allar.bank.outbox.dto.BalanceCreatedEvent;
import ee.allar.bank.outbox.dto.BalanceUpdatedEvent;
import ee.allar.bank.outbox.dto.TransactionCreatedEvent;
import ee.allar.bank.outbox.mapper.OutboxEventMapper;
import ee.allar.bank.transaction.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OutboxService {

    private static final String AGGREGATE_ACCOUNT = "ACCOUNT";
    private static final String BALANCE_TRANSACTION = "BALANCE";
    private static final String AGGREGATE_TRANSACTION = "TRANSACTION";

    private final OutboxEventMapper outboxEventMapper;
    private final ObjectMapper objectMapper;

    public void recordAccountCreated(Account account) {
        record(account.getId(), AGGREGATE_ACCOUNT, OutboxEventType.CREATED, AccountCreatedEvent.from(account));
    }

    public void recordBalanceCreated(Balance balance) {
        record(balance.getId(), BALANCE_TRANSACTION, OutboxEventType.CREATED, BalanceCreatedEvent.from(balance));
    }

    public void recordBalanceUpdated(Balance balance) {
        record(balance.getId(), BALANCE_TRANSACTION, OutboxEventType.UPDATED, BalanceUpdatedEvent.from(balance));
    }

    public void recordTransactionCreated(Transaction transaction) {
        record(transaction.getId(), AGGREGATE_TRANSACTION, OutboxEventType.CREATED, TransactionCreatedEvent.from(transaction));
    }

    @Transactional
    public List<OutboxEvent> findPendingForUpdate(int limit, int maxRetries) {
        return outboxEventMapper.findPendingForUpdate(limit, maxRetries);
    }

    @Transactional
    public void markPublished(UUID id) {
        outboxEventMapper.markPublished(id);
    }

    @Transactional
    public void updateRetryState(UUID id, int retryCount, Instant nextRetryAt, String lastError) {
        outboxEventMapper.updateRetryState(id, retryCount, nextRetryAt, lastError);
    }

    @Transactional
    public void markFailed(UUID id, String lastError) {
        outboxEventMapper.markFailed(id, lastError);
    }

    private void record(UUID aggregateId, String aggregateType, OutboxEventType eventType, Object payload) {
        String json = toJson(payload);
        OutboxEvent event = OutboxEvent.builder()
                .aggregateId(aggregateId)
                .aggregateType(aggregateType)
                .eventType(eventType)
                .payload(json)
                .status(OutboxEventStatus.PENDING)
                .build();
        outboxEventMapper.insert(event);
    }

    private String toJson(Object payload) {
        return objectMapper.writeValueAsString(payload);
    }
}
