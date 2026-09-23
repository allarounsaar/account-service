package ee.allar.bank.transaction.dto;

import ee.allar.bank.common.Currency;
import ee.allar.bank.common.deserializer.MoneyDeserializer;
import ee.allar.bank.transaction.Direction;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import tools.jackson.databind.annotation.JsonDeserialize;

import java.math.BigDecimal;

@Schema(description = "Request body for creating a transaction")
public record CreateTransactionRequest(
        @NotNull
        @Positive
        @JsonDeserialize(using = MoneyDeserializer.class)
        @Schema(description = "Transaction amount (up to 2 decimal places)", example = "99.99", type = "string")
        BigDecimal amount,

        @NotNull
        @Schema(description = "ISO 4217 currency code", example = "EUR")
        Currency currency,

        @NotNull
        @Schema(description = "Direction of transaction (IN, OUT)", example = "IN")
        Direction direction,

        @NotBlank
        @Schema(description = "Description of transaction", example = "Invoice payment")
        String description
) {
}
