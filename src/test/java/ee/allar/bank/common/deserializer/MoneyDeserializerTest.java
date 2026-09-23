package ee.allar.bank.common.deserializer;

import ee.allar.bank.common.exception.InvalidAmountException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class MoneyDeserializerTest {

    @Mock
    private JsonParser jsonParser;

    @Mock
    private DeserializationContext deserializationContext;

    private final MoneyDeserializer deserializer = new MoneyDeserializer();

    @Test
    void deserialize_validAmount_returnsFormattedBigDecimal() {
        doReturn("100.50").when(jsonParser).getString();

        BigDecimal result = deserializer.deserialize(jsonParser, deserializationContext);

        assertThat(result).isEqualTo(new BigDecimal("100.50"));
    }

    @Test
    void deserialize_validAmountWithTrailingZeros_returnsFormattedBigDecimal() {
        doReturn("100.500").when(jsonParser).getString();

        BigDecimal result = deserializer.deserialize(jsonParser, deserializationContext);

        assertThat(result).isEqualTo(new BigDecimal("100.50"));
    }

    @Test
    void deserialize_moreThanTwoDecimals_throwsInvalidAmountException() {
        doReturn("100.555").when(jsonParser).getString();

        assertThrows(InvalidAmountException.class, () -> deserializer.deserialize(jsonParser, deserializationContext));
    }

    @Test
    void deserialize_invalidAmount_throwsInvalidAmountException() {
        doReturn("invalid").when(jsonParser).getString();

        assertThrows(InvalidAmountException.class, () -> deserializer.deserialize(jsonParser, deserializationContext));
    }
}
