package com.mobelite.publisher.ui.utils;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.concurrent.ThreadLocalRandom;

public class generateDataUtils {
    public static LocalDate generateRandomBirthDate(int minAge, int maxAge) {
        int currentYear = LocalDate.now().getYear();
        int year = ThreadLocalRandom.current().nextInt(currentYear - maxAge, currentYear - minAge + 1);
        return generateRandomDateInYear(year);
    }


    public static LocalDate generateRandomBirthDate() {
        int currentYear = LocalDate.now().getYear();
        int year = ThreadLocalRandom.current().nextInt(1900, currentYear + 1);
        return generateRandomDateInYear(year);
    }


    public static LocalDate generateRandomDateInYear(int year) {
        int month = ThreadLocalRandom.current().nextInt(1, 13);
        int maxDay = Month.of(month).length(Year.isLeap(year));
        int day = ThreadLocalRandom.current().nextInt(1, maxDay + 1);
        return LocalDate.of(year, month, day);
    }

}
