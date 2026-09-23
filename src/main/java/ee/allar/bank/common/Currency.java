package ee.allar.bank.common;

import ee.allar.bank.common.deserializer.CurrencyDeserializer;
import tools.jackson.databind.annotation.JsonDeserialize;

import java.util.Arrays;
import java.util.List;

@JsonDeserialize(using = CurrencyDeserializer.class)
public enum Currency {
    EUR, SEK, GBP, USD;

    public static List<String> getNames() {
        return Arrays.stream(Currency.values())
                .map(Currency::name)
                .toList();
    }
}
