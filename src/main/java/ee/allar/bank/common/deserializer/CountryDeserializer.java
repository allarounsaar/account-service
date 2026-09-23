package ee.allar.bank.common.deserializer;

import ee.allar.bank.common.exception.InvalidCountryException;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

import java.util.Locale;
import java.util.Set;

public class CountryDeserializer extends ValueDeserializer<String> {

    private static final Set<String> ISO_COUNTRIES = Set.of(Locale.getISOCountries());

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctx) throws JacksonException {
        String value = p.getString();
        if (value != null) {
            String upper = value.toUpperCase(Locale.ROOT);
            if (ISO_COUNTRIES.contains(upper)) {
                return upper;
            }
        }
        throw new InvalidCountryException("Country must be valid ISO 3166-1 alpha-2 code");
    }
}
