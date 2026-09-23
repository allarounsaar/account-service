package ee.allar.bank.outbox.dto;

import ee.allar.bank.account.Account;

import java.time.Instant;
import java.util.UUID;

public record AccountCreatedEvent(
        UUID accountId,
        UUID customerId,
        String country,
        Instant createdAt
) {
    public static AccountCreatedEvent from(Account account) {
        return new AccountCreatedEvent(
                account.getId(),
                account.getCustomerId(),
                account.getCountry(),
                account.getCreatedAt()
        );
    }
}
