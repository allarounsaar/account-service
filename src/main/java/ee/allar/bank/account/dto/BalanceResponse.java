package ee.allar.bank.account.dto;

import ee.allar.bank.account.Balance;
import ee.allar.bank.common.Currency;
import ee.allar.bank.common.serializer.MoneySerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import tools.jackson.databind.annotation.JsonSerialize;

import java.math.BigDecimal;

@Schema(description = "Balance for a specific currency")
public record BalanceResponse(
        @JsonSerialize(using = MoneySerializer.class)
        @Schema(description = "Current available balance formatted with 2 decimal places", example = "0.00", type = "string")
        BigDecimal balance,
        @Schema(description = "ISO 4217 currency code", example = "EUR")
        Currency currency
) {
    public static BalanceResponse from(Balance balance) {
        return new BalanceResponse(balance.getBalance(), balance.getCurrency());
    }
}
