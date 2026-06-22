package org.ecal;

import java.time.LocalDate;
import java.util.Date;

public final class CalendarConversionCheck {
    private CalendarConversionCheck() {
    }

    public static void main(String[] args) {
        checkKnownDates();
        checkRoundTrips();
        if (hasIcu4j()) {
            checkAgainstIcu();
        } else {
            System.out.println("ICU4J not on classpath; skipped external calendar comparison.");
        }
        System.out.println("Calendar conversion checks passed.");
    }

    private static void checkKnownDates() {
        assertDate(LocalDate.of(2026, 6, 8), 1, 10, 2018);
        assertDate(LocalDate.of(2026, 6, 21), 14, 10, 2018);
        assertDate(LocalDate.of(2026, 6, 22), 15, 10, 2018);
        assertDate(LocalDate.of(2025, 9, 11), 1, 1, 2018);
        assertDate(LocalDate.of(2024, 9, 11), 1, 1, 2017);
    }

    private static void checkRoundTrips() {
        LocalDate start = LocalDate.of(1900, 1, 1);
        LocalDate end = LocalDate.of(2100, 12, 31);
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            EthiopianDate ethiopian = EthiopianDate.fromGregorian(date);
            LocalDate roundTrip = ethiopian.toGregorian();
            if (!date.equals(roundTrip)) {
                throw new AssertionError("Round trip failed: " + date + " -> " + ethiopian + " -> " + roundTrip);
            }
        }
    }

    private static void checkAgainstIcu() {
        try {
            Class<?> timeZoneClass = Class.forName("com.ibm.icu.util.TimeZone");
            Class<?> calendarClass = Class.forName("com.ibm.icu.util.Calendar");
            Class<?> gregorianClass = Class.forName("com.ibm.icu.util.GregorianCalendar");
            Class<?> ethiopicClass = Class.forName("com.ibm.icu.util.EthiopicCalendar");

            Object utc = timeZoneClass.getMethod("getTimeZone", String.class).invoke(null, "UTC");
            Object gregorian = gregorianClass.getConstructor(timeZoneClass).newInstance(utc);
            Object ethiopic = ethiopicClass.getConstructor(timeZoneClass).newInstance(utc);
            int dateField = calendarClass.getField("DATE").getInt(null);
            int monthField = calendarClass.getField("MONTH").getInt(null);
            int yearField = calendarClass.getField("YEAR").getInt(null);

            LocalDate start = LocalDate.of(1900, 1, 1);
            LocalDate end = LocalDate.of(2100, 12, 31);
            for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
                gregorianClass.getMethod("clear").invoke(gregorian);
                gregorianClass.getMethod("set", int.class, int.class, int.class)
                    .invoke(gregorian, date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth());
                ethiopicClass.getMethod("clear").invoke(ethiopic);
                Date gregorianTime = (Date) gregorianClass.getMethod("getTime").invoke(gregorian);
                ethiopicClass.getMethod("setTime", Date.class).invoke(ethiopic, gregorianTime);

                EthiopianDate actual = EthiopianDate.fromGregorian(date);
                int expectedDay = (int) ethiopicClass.getMethod("get", int.class).invoke(ethiopic, dateField);
                int expectedMonth = (int) ethiopicClass.getMethod("get", int.class).invoke(ethiopic, monthField) + 1;
                int expectedYear = (int) ethiopicClass.getMethod("get", int.class).invoke(ethiopic, yearField);
                if (actual.day() != expectedDay || actual.month() != expectedMonth || actual.year() != expectedYear) {
                    throw new AssertionError(
                        "ICU mismatch for " + date
                            + ": expected " + expectedDay + "/" + expectedMonth + "/" + expectedYear
                            + " but got " + actual.day() + "/" + actual.month() + "/" + actual.year()
                    );
                }
            }
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError("Unable to run ICU4J comparison", exception);
        }
    }

    private static boolean hasIcu4j() {
        try {
            Class.forName("com.ibm.icu.util.EthiopicCalendar");
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }

    private static void assertDate(LocalDate gregorian, int day, int month, long year) {
        EthiopianDate actual = EthiopianDate.fromGregorian(gregorian);
        if (actual.day() != day || actual.month() != month || actual.year() != year) {
            throw new AssertionError(
                "Expected " + gregorian + " to be " + day + "/" + month + "/" + year
                    + " but got " + actual.day() + "/" + actual.month() + "/" + actual.year()
            );
        }
    }
}
