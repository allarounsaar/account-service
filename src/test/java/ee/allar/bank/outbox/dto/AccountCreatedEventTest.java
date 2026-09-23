package ee.allar.bank.outbox.dto;

import ee.allar.bank.account.Account;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class AccountCreatedEventTest {

    private static final UUID ACCOUNT_ID = UUID.fromString("01a0c35e-e723-767d-9265-60d0d5a07bf7");
    private static final UUID CUSTOMER_ID = UUID.fromString("01a0c35f-c90b-7054-b507-06caa681e380");
    private static final Instant CREATED_AT = Instant.MIN;
    private static final Instant UPDATED_AT = Instant.MAX;

    @Test
    void from_account_mapsToAccountCreatedEvent() {
        Account account = Account.builder()
                .id(ACCOUNT_ID)
                .customerId(CUSTOMER_ID)
                .country("EE")
                .createdAt(CREATED_AT)
                .updatedAt(UPDATED_AT)
                .build();

        AccountCreatedEvent event = AccountCreatedEvent.from(account);

        assertThat(event).isNotNull();
        assertAll(
                () -> assertThat(event.accountId()).isEqualTo(ACCOUNT_ID),
                () -> assertThat(event.customerId()).isEqualTo(CUSTOMER_ID),
                () -> assertThat(event.country()).isEqualTo("EE"),
                () -> assertThat(event.createdAt()).isEqualTo(CREATED_AT)
        );
    }

}