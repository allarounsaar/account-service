package ee.allar.bank.common.deserializer;

import ee.allar.bank.common.exception.InvalidCountryException;
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
class CountryDeserializerTest {

    @Mock
    private JsonParser jsonParser;

    @Mock
    private DeserializationContext deserializationContext;

    private final CountryDeserializer deserializer = new CountryDeserializer();

    @Test
    void deserialize_validCountry_returnsCountryCode() {
        doReturn("ee").when(jsonParser).getString();

        String result = deserializer.deserialize(jsonParser, deserializationContext);

        assertThat(result).isEqualTo("EE");
    }

    @Test
    void deserialize_invalidCountry_throwsException() {
        doReturn("EST").when(jsonParser).getString();

        assertThrows(InvalidCountryException.class, () -> deserializer.deserialize(jsonParser, deserializationContext));
    }
}
