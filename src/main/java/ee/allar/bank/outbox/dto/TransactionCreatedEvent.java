package ee.allar.bank.outbox.dto;

import ee.allar.bank.common.Currency;
import ee.allar.bank.transaction.Direction;
import ee.allar.bank.transaction.Transaction;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionCreatedEvent(
        UUID accountId,
        UUID transactionId,
        BigDecimal amount,
        Currency currency,
        Direction direction,
        String description,
        BigDecimal balanceAfter,
        Instant createdAt
) {
    public static TransactionCreatedEvent from(Transaction transaction) {
        return new TransactionCreatedEvent(
                transaction.getAccountId(),
                transaction.getId(),
                transaction.getAmount(),
                transaction.getCurrency(),
                transaction.getDirection(),
                transaction.getDescription(),
                transaction.getBalanceAfter(),
                transaction.getCreatedAt()
        );
    }
}
