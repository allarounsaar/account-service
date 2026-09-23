package ee.allar.bank.outbox.dto;

import ee.allar.bank.account.Balance;
import ee.allar.bank.common.Currency;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record BalanceCreatedEvent(
        UUID balanceId,
        UUID accountId,
        Currency currency,
        BigDecimal balance,
        Instant createdAt
) {
    public static BalanceCreatedEvent from(Balance balance) {
        return new BalanceCreatedEvent(
                balance.getId(),
                balance.getAccountId(),
                balance.getCurrency(),
                balance.getBalance(),
                balance.getCreatedAt()
        );
    }
}
