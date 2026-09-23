package ee.allar.bank.common.deserializer;

import ee.allar.bank.common.exception.InvalidDirectionException;
import ee.allar.bank.transaction.Direction;
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
class DirectionDeserializerTest {

    @Mock
    private JsonParser jsonParser;

    @Mock
    private DeserializationContext deserializationContext;

    private final DirectionDeserializer deserializer = new DirectionDeserializer();

    @Test
    void deserialize_validDirection_returnsDirection() {
        doReturn("in").when(jsonParser).getString();

        Direction result = deserializer.deserialize(jsonParser, deserializationContext);

        assertThat(result).isEqualTo(Direction.IN);
    }

    @Test
    void deserialize_invalidDirection_throwsException() {
        doReturn("withdraw").when(jsonParser).getString();

        assertThrows(InvalidDirectionException.class, () -> deserializer.deserialize(jsonParser, deserializationContext));
    }
}
