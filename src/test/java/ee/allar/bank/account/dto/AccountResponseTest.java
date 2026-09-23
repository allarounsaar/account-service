package ee.allar.bank.account.dto;

import ee.allar.bank.account.Account;
import ee.allar.bank.account.Balance;
import ee.allar.bank.common.Currency;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class AccountResponseTest {

    private static final UUID ACCOUNT_ID = UUID.fromString("01a0c35e-e723-767d-9265-60d0d5a07bf7");
    private static final UUID CUSTOMER_ID = UUID.fromString("01a0c35f-c90b-7054-b507-06caa681e380");

    @Test
    void from_accountAndBalance_mapsToAccountResponse() {
        Account account = Account.builder().id(ACCOUNT_ID).customerId(CUSTOMER_ID).build();
        List<Balance> balances = List.of(
                Balance.builder().balance(BigDecimal.ZERO).currency(Currency.USD).build(),
                Balance.builder().balance(BigDecimal.TEN).currency(Currency.EUR).build()
        );

        AccountResponse response = AccountResponse.from(account, balances);

        assertThat(response).isNotNull();
        assertThat(response.balances()).hasSize(2);
        assertAll(
                () ->  assertThat(response.accountId()).isEqualTo(ACCOUNT_ID),
                () ->  assertThat(response.customerId()).isEqualTo(CUSTOMER_ID),
                () ->  assertThat(response.balances().getFirst().balance()).isEqualTo(BigDecimal.ZERO),
                () ->  assertThat(response.balances().getFirst().currency()).isEqualTo(Currency.USD),
                () ->  assertThat(response.balances().getLast().balance()).isEqualTo(BigDecimal.TEN),
                () ->  assertThat(response.balances().getLast().currency()).isEqualTo(Currency.EUR)
        );
    }
}