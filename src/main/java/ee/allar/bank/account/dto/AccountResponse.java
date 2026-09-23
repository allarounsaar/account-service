package ee.allar.bank.account.dto;

import ee.allar.bank.account.Account;
import ee.allar.bank.account.Balance;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

@Schema(description = "Account details with associated balances")
public record AccountResponse(
        @Schema(description = "Unique account identifier", example = "01a0c40a-66bc-74c2-a193-ad672b487816")
        UUID accountId,
        @Schema(description = "Unique customer identifier", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID customerId,
        @Schema(description = "List of currency balances")
        List<BalanceResponse> balances
) {
    public static AccountResponse from(Account account, List<Balance> balances) {
        List<BalanceResponse> balanceResponses = balances.stream()
                .map(BalanceResponse::from)
                .toList();
        return new AccountResponse(account.getId(), account.getCustomerId(), balanceResponses);
    }
}
