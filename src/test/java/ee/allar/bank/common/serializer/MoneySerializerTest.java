package ee.allar.bank.common.serializer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;

import java.math.BigDecimal;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MoneySerializerTest {

    @Mock
    private JsonGenerator jsonGenerator;

    @Mock
    private SerializationContext serializationContext;

    private final MoneySerializer serializer = new MoneySerializer();

    @Test
    void serialize_nullValue_writesNull() {
        serializer.serialize(null, jsonGenerator, serializationContext);

        verify(jsonGenerator, times(1)).writeNull();
    }

    @Test
    void serialize_validValue_writesFormattedNumber() {
        serializer.serialize(new BigDecimal("100.5"), jsonGenerator, serializationContext);

        verify(jsonGenerator, times(1)).writeNumber(new BigDecimal("100.50"));
    }

    @Test
    void serialize_fourDecimalsValue_roundsToTwoDecimals() {
        serializer.serialize(new BigDecimal("100.5555"), jsonGenerator, serializationContext);

        verify(jsonGenerator, times(1)).writeNumber(new BigDecimal("100.56"));
    }
}
