package ee.allar.bank.outbox.dto;

import ee.allar.bank.common.Currency;
import ee.allar.bank.transaction.Direction;
import ee.allar.bank.transaction.Transaction;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class TransactionCreatedEventTest {

    private static final UUID ACCOUNT_ID = UUID.fromString("01a0c35e-e723-767d-9265-60d0d5a07bf7");
    private static final UUID TRANSACTION_ID = UUID.fromString("01a0c35f-c90b-7054-b507-06caa681e380");
    private static final Instant CREATED_AT = Instant.MIN;
    private static final Instant UPDATED_AT = Instant.MAX;

    @Test
    void from_transaction_mapsToTransactionCreatedEvent() {
        Transaction transaction = Transaction.builder()
                .id(TRANSACTION_ID)
                .accountId(ACCOUNT_ID)
                .amount(BigDecimal.ONE)
                .currency(Currency.EUR)
                .direction(Direction.IN)
                .description("description")
                .balanceAfter(BigDecimal.TEN)
                .createdAt(CREATED_AT)
                .updatedAt(UPDATED_AT)
                .build();

        TransactionCreatedEvent event = TransactionCreatedEvent.from(transaction);

        assertThat(event).isNotNull();
        assertAll(
                () -> assertThat(event.accountId()).isEqualTo(ACCOUNT_ID),
                () -> assertThat(event.transactionId()).isEqualTo(TRANSACTION_ID),
                () -> assertThat(event.amount()).isEqualTo(BigDecimal.ONE),
                () -> assertThat(event.currency()).isEqualTo(Currency.EUR),
                () -> assertThat(event.direction()).isEqualTo(Direction.IN),
                () -> assertThat(event.description()).isEqualTo("description"),
                () -> assertThat(event.balanceAfter()).isEqualTo(BigDecimal.TEN),
                () -> assertThat(event.createdAt()).isEqualTo(CREATED_AT)
        );
    }
}