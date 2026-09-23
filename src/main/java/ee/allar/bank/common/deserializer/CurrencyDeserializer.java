package ee.allar.bank.common.deserializer;

import ee.allar.bank.common.Currency;
import ee.allar.bank.common.exception.InvalidCurrencyException;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

public class CurrencyDeserializer extends ValueDeserializer<Currency> {
    @Override
    public Currency deserialize(JsonParser p, DeserializationContext ctx) throws JacksonException {
        String value = p.getString();
        try {
            return Currency.valueOf(value.toUpperCase());
        } catch (Exception e) {
            throw new InvalidCurrencyException(String.format("Invalid currency: %s. Allowed values: %s", value, Currency.getNames()));
        }
    }
}
