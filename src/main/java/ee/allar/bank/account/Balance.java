package ee.allar.bank.account;

import ee.allar.bank.common.Currency;
import ee.allar.bank.common.MoneyUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Balance {
    private UUID id;
    private UUID accountId;
    private Currency currency;
    @Builder.Default
    private BigDecimal balance = MoneyUtils.toTwoDecimals(BigDecimal.ZERO);
    private Instant createdAt;
    private Instant updatedAt;

    public Balance(UUID accountId, Currency currency)  {
        this.accountId = accountId;
        this.currency = currency;
        this.balance = MoneyUtils.toTwoDecimals(BigDecimal.ZERO);
    }
}
