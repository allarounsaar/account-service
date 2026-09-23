package ee.allar.bank.common;

import ee.allar.bank.common.exception.InvalidAmountException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MoneyUtilsTest {

    @Test
    void toTwoDecimals_validAmount_returnsFormattedBigDecimalWithTwoDecimals() {
        BigDecimal formatted = MoneyUtils.toTwoDecimals(new BigDecimal("100.5"));

        assertThat(formatted).isEqualTo(new BigDecimal("100.50"));
    }

    @Test
    void toFourDecimals_validAmount_returnsFormattedBigDecimalWithFourDecimals() {
        BigDecimal formatted = MoneyUtils.toFourDecimals(new BigDecimal("100.5"));

        assertThat(formatted).isEqualTo(new BigDecimal("100.5000"));
    }

    @Test
    void toScale_withCustomScale_returnsFormattedBigDecimal() {
        BigDecimal formatted = MoneyUtils.toScale(new BigDecimal("100.5"), 3);

        assertThat(formatted).isEqualTo(new BigDecimal("100.500"));
    }

    @Test
    void toTwoDecimals_roundsUsingHalfEven() {
        BigDecimal roundedDown = MoneyUtils.toTwoDecimals(new BigDecimal("100.545"));
        BigDecimal roundedUp = MoneyUtils.toTwoDecimals(new BigDecimal("100.555"));

        assertThat(roundedDown).isEqualTo(new BigDecimal("100.54"));
        assertThat(roundedUp).isEqualTo(new BigDecimal("100.56"));
    }

    @Test
    void toScale_nullAmount_throwsException() {
        assertThrows(InvalidAmountException.class, () -> MoneyUtils.toTwoDecimals(null));
        assertThrows(InvalidAmountException.class, () -> MoneyUtils.toFourDecimals(null));
        assertThrows(InvalidAmountException.class, () -> MoneyUtils.toScale(null, 2));
    }
}
