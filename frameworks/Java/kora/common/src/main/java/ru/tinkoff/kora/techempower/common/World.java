package ru.tinkoff.kora.techempower.common;

import java.util.concurrent.ThreadLocalRandom;

public record World(int id, int randomNumber) {
    public static int randomWorldNumber(int previousRead) {
        var random = randomWorldNumber();
        while (random == previousRead) {
            random = randomWorldNumber();
        }
        return random;
    }

    public static int randomWorldNumber() {
        return 1 + ThreadLocalRandom.current().nextInt(10000);
    }

    public static int parseQueryCount(String textValue) {
        if (textValue == null) {
            return 1;
        }
        int parsedValue;
        try {
            parsedValue = Integer.parseInt(textValue);
        } catch (NumberFormatException e) {
            return 1;
        }
        return Math.min(500, Math.max(1, parsedValue));
    }
}
