package ee.allar.bank.common.deserializer;

import ee.allar.bank.common.MoneyUtils;
import ee.allar.bank.common.exception.InvalidAmountException;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

import java.math.BigDecimal;

public class MoneyDeserializer extends ValueDeserializer<BigDecimal> {
    @Override
    public BigDecimal deserialize(JsonParser p, DeserializationContext ctx) throws JacksonException {
        String value = p.getString();
        try {
            BigDecimal amount = new BigDecimal(value);
            if (amount.scale() > 0 && amount.stripTrailingZeros().scale() > 2) {
                throw new InvalidAmountException("Amount cannot have more than 2 decimal places: " + value);
            }
            return MoneyUtils.toTwoDecimals(amount);
        } catch (InvalidAmountException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidAmountException("Invalid amount format: " + value);
        }
    }
}
