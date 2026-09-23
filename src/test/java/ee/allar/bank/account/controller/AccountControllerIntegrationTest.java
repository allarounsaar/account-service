package ee.allar.bank.account.controller;


import ee.allar.bank.BaseIntegrationTest;
import ee.allar.bank.account.dto.AccountResponse;
import ee.allar.bank.account.dto.BalanceResponse;

import ee.allar.bank.common.Currency;
import ee.allar.bank.common.MoneyUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AccountControllerIntegrationTest extends BaseIntegrationTest {

    private static final UUID ACCOUNT_ID = UUID.fromString("01a0c40a-66bc-74c2-a193-ad672b487816");
    private static final UUID CUSTOMER_ID = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6");

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void createAccount_withValidRequest_returnsCreatedAccountWithBalances() {
        String requestBody = """
                {
                  "customerId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                  "country": "ee",
                  "currencies": ["eur", "usd"]
                }
                """;

        AccountResponse response = webTestClient.post()
                .uri("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(AccountResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        assertThat(response.accountId()).isNotNull();
        assertThat(response.customerId()).isEqualTo(CUSTOMER_ID);
        assertThat(response.balances()).containsExactlyInAnyOrder(
                new BalanceResponse(MoneyUtils.toTwoDecimals(BigDecimal.ZERO), Currency.EUR),
                new BalanceResponse(MoneyUtils.toTwoDecimals(BigDecimal.ZERO), Currency.USD));

        List<Map<String, Object>> accountEvents = findOutboxEvents("ACCOUNT", response.accountId());
        assertThat(accountEvents).hasSize(1);
        assertThat(accountEvents.getFirst().get("event_type")).isEqualTo("CREATED");
        assertThat(accountEvents.getFirst().get("status")).isIn("PENDING", "PUBLISHED");

        List<Map<String, Object>> balanceEvents = findOutboxEvents("BALANCE");
        assertThat(balanceEvents).hasSize(2);
        assertThat(balanceEvents).allSatisfy(event -> {
            assertThat(event.get("event_type")).isEqualTo("CREATED");
            assertThat(event.get("status")).isIn("PENDING", "PUBLISHED");
        });
    }

    @Test
    @Sql("/sql/account.sql")
    void getAccount_getAccountById_returnsAccountWithBalances() {
        AccountResponse response = webTestClient.get()
                .uri("/api/accounts/{id}", ACCOUNT_ID.toString())
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(AccountResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        assertThat(response.accountId()).isEqualTo(ACCOUNT_ID);
        assertThat(response.customerId()).isEqualTo(CUSTOMER_ID);
        assertThat(response.balances()).containsExactly(
                new BalanceResponse(new BigDecimal("1.11"), Currency.EUR),
                new BalanceResponse(new BigDecimal("2.22"), Currency.GBP),
                new BalanceResponse(new BigDecimal("3.33"), Currency.USD),
                new BalanceResponse(new BigDecimal("4.44"), Currency.SEK));
    }
}
