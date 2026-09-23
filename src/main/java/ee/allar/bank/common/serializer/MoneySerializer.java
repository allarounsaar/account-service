package ee.allar.bank.common.serializer;

import ee.allar.bank.common.MoneyUtils;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

import java.math.BigDecimal;

public class MoneySerializer extends ValueSerializer<BigDecimal> {

    @Override
    public void serialize(BigDecimal value, JsonGenerator gen, SerializationContext ctx) throws JacksonException {
        if (value == null) {
            gen.writeNull();
        } else {
            gen.writeNumber(MoneyUtils.toTwoDecimals(value));
        }
    }
}
