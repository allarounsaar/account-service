package ee.allar.bank.transaction.controller;

import ee.allar.bank.BaseIntegrationTest;
import ee.allar.bank.common.Currency;
import ee.allar.bank.common.MoneyUtils;
import ee.allar.bank.transaction.Direction;
import ee.allar.bank.transaction.dto.TransactionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class TransactionControllerIntegrationTest extends BaseIntegrationTest {

    private static final UUID ACCOUNT_ID = UUID.fromString("01a0c40a-66bc-74c2-a193-ad672b487816");

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        webTestClient = webTestClient.mutate()
                .responseTimeout(Duration.ofSeconds(30))
                .build();
    }

    @Test
    @Sql("/sql/account.sql")
    void createAccount_withValidRequest_returnsCreatedAccountWithBalances() {
        String requestBody = """
                {
                  "amount": 99.99,
                  "currency": "eur",
                  "direction": "in",
                  "description": "invoice payment"
                }
                """;

        TransactionResponse response = webTestClient.post()
                .uri("/api/accounts/{accountId}/transactions", ACCOUNT_ID.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(TransactionResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        assertAll(
                () -> assertThat(response.accountId()).isEqualTo(ACCOUNT_ID),
                () -> assertThat(response.transactionId()).isNotNull(),
                () -> assertThat(response.amount()).isEqualTo(MoneyUtils.toTwoDecimals(new BigDecimal("99.99"))),
                () -> assertThat(response.currency()).isEqualTo(Currency.EUR),
                () -> assertThat(response.direction()).isEqualTo(Direction.IN),
                () -> assertThat(response.description()).isEqualTo("invoice payment"),
                () -> assertThat(response.balanceAfter()).isEqualTo(MoneyUtils.toTwoDecimals(new BigDecimal("101.10")))
        );

        List<Map<String, Object>> transactionEvents = findOutboxEvents("TRANSACTION", response.transactionId());
        assertThat(transactionEvents).hasSize(1);
        assertThat(transactionEvents.getFirst().get("event_type")).isEqualTo("CREATED");
        assertThat(transactionEvents.getFirst().get("status")).isIn("PENDING", "PUBLISHED");
    }

    @Test
    @Sql("/sql/account.sql")
    void createTransaction_withMoreThanTwoDecimals_returnsBadRequest() {
        String requestBody = """
                {
                  "amount": 99.999,
                  "currency": "eur",
                  "direction": "in",
                  "description": "invoice payment"
                }
                """;

        webTestClient.post()
                .uri("/api/accounts/{accountId}/transactions", ACCOUNT_ID.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("INVALID_AMOUNT")
                .jsonPath("$.message").isEqualTo("Amount cannot have more than 2 decimal places: 99.999");
    }

    @Test
    @Sql("/sql/account.sql")
    @Sql("/sql/transactions.sql")
    void getTransactions_withValidRequest_returnsTransactionsList() {

        List<TransactionResponse> response = webTestClient.get()
                .uri("/api/accounts/{accountId}/transactions", ACCOUNT_ID.toString())
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(TransactionResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        assertThat(response).hasSize(4);
        assertThat(response)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("transactionId")
                .containsExactlyInAnyOrder(
                        new TransactionResponse(ACCOUNT_ID, null, new BigDecimal("199.00"), Currency.SEK, Direction.IN, "SEK IN", new BigDecimal("199.00")),
                        new TransactionResponse(ACCOUNT_ID, null, new BigDecimal("299.00"), Currency.GBP, Direction.IN, "GBP IN", new BigDecimal("299.00")),
                        new TransactionResponse(ACCOUNT_ID, null, new BigDecimal("11.11"), Currency.SEK, Direction.OUT, "SEK OUT", new BigDecimal("187.89")),
                        new TransactionResponse(ACCOUNT_ID, null, new BigDecimal("22.22"), Currency.GBP, Direction.OUT, "GBP OUT", new BigDecimal("276.78"))
                );
        assertThat(response).allSatisfy(item -> assertThat(item.transactionId()).isNotNull());
    }
}
