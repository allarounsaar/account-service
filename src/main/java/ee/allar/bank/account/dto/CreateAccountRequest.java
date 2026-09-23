package ee.allar.bank.account.dto;

import ee.allar.bank.common.Currency;
import ee.allar.bank.common.deserializer.CountryDeserializer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import tools.jackson.databind.annotation.JsonDeserialize;

import java.util.Set;
import java.util.UUID;

@Schema(description = "Request body for creating a new bank account")
public record CreateAccountRequest(
        @NotNull
        @Schema(description = "Unique customer identifier", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID customerId,

        @NotBlank
        @JsonDeserialize(using = CountryDeserializer.class)
        @Schema(description = "ISO 3166-1 alpha-2 two-letter country code", example = "EE")
        String country,

        @NotEmpty
        @Schema(description = "Set of initial currencies to create balances for", example = "[\"EUR\", \"USD\"]")
        Set<Currency> currencies
) {
}
