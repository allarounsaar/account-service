package ee.allar.bank.transaction.dto;

import ee.allar.bank.common.Currency;
import ee.allar.bank.transaction.Direction;
import ee.allar.bank.transaction.Transaction;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class TransactionResponseTest {

    private static final UUID ACCOUNT_ID = UUID.fromString("01a0c35e-e723-767d-9265-60d0d5a07bf7");
    private static final UUID TRANSACTION_ID = UUID.fromString("01a0c35f-c90b-7054-b507-06caa681e380");

    @Test
    void from_transaction_mapsToTransactionResponse() {
        Transaction transaction = Transaction.builder()
                .id(TRANSACTION_ID)
                .accountId(ACCOUNT_ID)
                .amount(new BigDecimal("100.5000"))
                .currency(Currency.EUR)
                .direction(Direction.IN)
                .description("Salary payment")
                .balanceAfter(new BigDecimal("250.7500"))
                .build();

        TransactionResponse response = TransactionResponse.from(transaction);

        assertThat(response).isNotNull();
        assertAll(
                () -> assertThat(response.accountId()).isEqualTo(ACCOUNT_ID),
                () -> assertThat(response.transactionId()).isEqualTo(TRANSACTION_ID),
                () -> assertThat(response.amount()).isEqualTo(new BigDecimal("100.5000")),
                () -> assertThat(response.currency()).isEqualTo(Currency.EUR),
                () -> assertThat(response.direction()).isEqualTo(Direction.IN),
                () -> assertThat(response.description()).isEqualTo("Salary payment"),
                () -> assertThat(response.balanceAfter()).isEqualTo(new BigDecimal("250.7500"))
        );
    }
}
