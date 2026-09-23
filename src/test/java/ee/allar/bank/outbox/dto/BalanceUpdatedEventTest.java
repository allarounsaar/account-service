package ee.allar.bank.outbox.dto;

import ee.allar.bank.account.Balance;
import ee.allar.bank.common.Currency;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class BalanceUpdatedEventTest {

    private static final UUID BALANCE_ID = UUID.fromString("01a0c35f-c90b-7054-b507-06caa681e380");
    private static final UUID ACCOUNT_ID = UUID.fromString("01a0c35e-e723-767d-9265-60d0d5a07bf7");
    private static final Instant CREATED_AT = Instant.MIN;
    private static final Instant UPDATED_AT = Instant.MAX;


    @Test
    void from_balance_mapsToBalanceUpdatedEvent() {
        Balance account = Balance.builder()
                .id(BALANCE_ID)
                .accountId(ACCOUNT_ID)
                .currency(Currency.EUR)
                .balance(BigDecimal.TEN)
                .createdAt(CREATED_AT)
                .updatedAt(UPDATED_AT)
                .build();

        BalanceUpdatedEvent event = BalanceUpdatedEvent.from(account);

        assertThat(event).isNotNull();
        assertAll(
                () -> assertThat(event.balanceId()).isEqualTo(BALANCE_ID),
                () -> assertThat(event.accountId()).isEqualTo(ACCOUNT_ID),
                () -> assertThat(event.currency()).isEqualTo(Currency.EUR),
                () -> assertThat(event.balance()).isEqualTo(BigDecimal.TEN),
                () -> assertThat(event.updatedAt()).isEqualTo(UPDATED_AT)
        );
    }
}