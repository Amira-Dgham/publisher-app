package com.mobelite.e2e.shared.helpers;

import java.time.LocalDate;
import java.time.Month;
import java.util.concurrent.ThreadLocalRandom;

public class GenerateData {

    public static LocalDate generateRandomBirthDate(int minAge, int maxAge) {
        int currentYear = LocalDate.now().getYear();
        int randomYear = ThreadLocalRandom.current().nextInt(currentYear - maxAge, currentYear - minAge + 1);
        int month = ThreadLocalRandom.current().nextInt(1, 13);
        int day = ThreadLocalRandom.current().nextInt(1, Month.of(month).length(false) + 1);
        return LocalDate.of(randomYear, month, day);
    }
}
