package ee.allar.bank.transaction.dto;

import ee.allar.bank.common.Currency;
import ee.allar.bank.common.serializer.MoneySerializer;
import ee.allar.bank.transaction.Direction;
import ee.allar.bank.transaction.Transaction;
import io.swagger.v3.oas.annotations.media.Schema;
import tools.jackson.databind.annotation.JsonSerialize;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Transaction response details")
public record TransactionResponse(
        @Schema(description = "Account identifier", example = "01a0c40a-66bc-74c2-a193-ad672b487816")
        UUID accountId,
        @Schema(description = "Transaction identifier", example = "01a0c40a-66bc-74c2-a193-ad672b487817")
        UUID transactionId,
        @JsonSerialize(using = MoneySerializer.class)
        @Schema(description = "Transaction amount formatted with 2 decimal places", example = "99.99", type = "string")
        BigDecimal amount,
        @Schema(description = "ISO 4217 currency code", example = "EUR")
        Currency currency,
        @Schema(description = "Direction of transaction: IN or OUT", example = "IN")
        Direction direction,
        @Schema(description = "Description of transaction", example = "Invoice payment")
        String description,
        @JsonSerialize(using = MoneySerializer.class)
        @Schema(description = "Account balance after transaction execution", example = "101.10", type = "string")
        BigDecimal balanceAfter
) {
    public static TransactionResponse from(Transaction transaction) {
        return new TransactionResponse(
                transaction.getAccountId(),
                transaction.getId(),
                transaction.getAmount(),
                transaction.getCurrency(),
                transaction.getDirection(),
                transaction.getDescription(),
                transaction.getBalanceAfter()
        );
    }
}
