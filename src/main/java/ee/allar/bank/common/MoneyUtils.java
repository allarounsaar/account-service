package ee.allar.bank.common;

import ee.allar.bank.common.exception.InvalidAmountException;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.RoundingMode;

@UtilityClass
public final class MoneyUtils {
    public static final int TWO_DECIMALS = 2;
    public static final int FOUR_DECIMALS = 4;
    private static final RoundingMode DEFAULT_ROUNDING = RoundingMode.HALF_EVEN;

    public static BigDecimal toTwoDecimals(BigDecimal amount) {
        return toScale(amount, TWO_DECIMALS);
    }

    public static BigDecimal toFourDecimals(BigDecimal amount) {
        return toScale(amount, FOUR_DECIMALS);
    }

    public static BigDecimal toScale(BigDecimal amount, int scale) {
        if (amount == null) {
            throw new InvalidAmountException("Amount cannot be null");
        }
        return amount.setScale(scale, DEFAULT_ROUNDING);
    }
}
