package ee.allar.bank.common.deserializer;

import ee.allar.bank.common.Currency;
import ee.allar.bank.common.exception.InvalidCurrencyException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class CurrencyDeserializerTest {

    @Mock
    private JsonParser jsonParser;

    @Mock
    private DeserializationContext deserializationContext;

    private final CurrencyDeserializer deserializer = new CurrencyDeserializer();

    @Test
    void deserialize_validCurrency_returnsCurrency() {
        doReturn("eur").when(jsonParser).getString();

        Currency result = deserializer.deserialize(jsonParser, deserializationContext);

        assertThat(result).isEqualTo(Currency.EUR);
    }

    @Test
    void deserialize_invalidCurrency_throwsException() {
        doReturn("€").when(jsonParser).getString();

        assertThrows(InvalidCurrencyException.class, () -> deserializer.deserialize(jsonParser, deserializationContext));
    }
}
