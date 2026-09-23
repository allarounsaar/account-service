package ee.allar.bank.transaction;

import ee.allar.bank.common.deserializer.DirectionDeserializer;
import tools.jackson.databind.annotation.JsonDeserialize;

import java.util.Arrays;
import java.util.List;

@JsonDeserialize(using = DirectionDeserializer.class)
public enum Direction {
    IN, OUT;

    public static List<String> getNames() {
        return Arrays.stream(Direction.values())
                .map(Direction::name)
                .toList();
    }
}
