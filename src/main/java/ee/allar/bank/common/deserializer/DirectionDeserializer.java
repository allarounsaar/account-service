package ee.allar.bank.common.deserializer;

import ee.allar.bank.common.exception.InvalidDirectionException;
import ee.allar.bank.transaction.Direction;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

public class DirectionDeserializer extends ValueDeserializer<Direction> {
    @Override
    public Direction deserialize(JsonParser p, DeserializationContext ctx) throws JacksonException {
        String value = p.getString();
        try {
            return Direction.valueOf(value.toUpperCase());
        } catch (Exception e) {
            throw new InvalidDirectionException(String.format("Invalid direction: %s. Allowed values: %s", value, Direction.getNames()));
        }
    }
}
