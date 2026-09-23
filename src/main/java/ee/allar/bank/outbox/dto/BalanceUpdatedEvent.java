package ee.allar.bank.outbox.dto;

import ee.allar.bank.account.Balance;
import ee.allar.bank.common.Currency;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record BalanceUpdatedEvent(
        UUID balanceId,
        UUID accountId,
        Currency currency,
        BigDecimal balance,
        Instant updatedAt
) {
    public static BalanceUpdatedEvent from(Balance balance) {
        return new BalanceUpdatedEvent(
                balance.getId(),
                balance.getAccountId(),
                balance.getCurrency(),
                balance.getBalance(),
                balance.getUpdatedAt()
        );
    }
}