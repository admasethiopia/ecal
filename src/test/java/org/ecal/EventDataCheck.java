package org.ecal;

import java.time.LocalDate;
import java.util.Map;

/**
 * Verifies the movable-feast engine (Bahire Hasab) and the routine/notable
 * event classification.
 *
 * <p>The movable feasts are anchored to Easter (ትንሳኤ), so we cross-check the
 * computed Easter against an independent Orthodox (Julian) Computus for every
 * year in range. This catches errors in the Bahire Hasab arithmetic that the
 * date-conversion tests cannot see.
 */
public final class EventDataCheck {
    private static final String EASTER = "ትንሳኤ";

    private EventDataCheck() {
    }

    public static void main(String[] args) {
        run();
    }

    static void run() {
        checkEasterAgainstComputus();
        checkEventClassification();
        System.out.println("Event data checks passed.");
    }

    private static void checkEasterAgainstComputus() {
        // +13 day Julian-to-Gregorian offset holds for 1900-03-01 .. 2100-02-28.
        for (int gregorianYear = 1910; gregorianYear <= 2099; gregorianYear++) {
            LocalDate expected = orthodoxEaster(gregorianYear);
            long ethiopianYear = gregorianYear - 8L;
            EthiopianDate easter = findMovableFeast(ethiopianYear, EASTER);
            if (easter == null) {
                throw new AssertionError("No " + EASTER + " computed for Ethiopian year " + ethiopianYear);
            }
            LocalDate actual = easter.toGregorian();
            if (!actual.equals(expected)) {
                throw new AssertionError(
                    "Easter mismatch for Gregorian " + gregorianYear
                        + ": Computus says " + expected
                        + " but app computed " + actual + " (" + easter + ")"
                );
            }
        }
    }

    private static void checkEventClassification() {
        // New year carries the routine commemoration plus several notable feasts.
        EthiopianDate newYear = new EthiopianDate(1, 1, 2018);
        if (!EventData.hasNotableEvents(newYear)) {
            throw new AssertionError("Expected notable events on the Ethiopian new year");
        }
        long routineCount = EventData.eventsFor(newYear).stream().filter(CalendarEvent::routine).count();
        if (routineCount != 1) {
            throw new AssertionError("Expected exactly one routine commemoration on 1/1, got " + routineCount);
        }

        // A plain day carries only the routine commemoration.
        EthiopianDate plainDay = new EthiopianDate(8, 1, 2018);
        if (EventData.hasNotableEvents(plainDay)) {
            throw new AssertionError("8/1 should have no notable events: " + EventData.notableEventsFor(plainDay));
        }
        if (EventData.eventsFor(plainDay).size() != 1) {
            throw new AssertionError("8/1 should carry exactly the routine commemoration");
        }
    }

    private static EthiopianDate findMovableFeast(long ethiopianYear, String name) {
        for (Map.Entry<String, String> entry : EventData.movableEvents(ethiopianYear).entrySet()) {
            if (entry.getValue().equals(name)) {
                String[] parts = entry.getKey().split("/");
                int month = Integer.parseInt(parts[0]);
                int day = Integer.parseInt(parts[1]);
                return new EthiopianDate(day, month, ethiopianYear);
            }
        }
        return null;
    }

    /** Orthodox (Julian) Easter as a Gregorian date, via the Meeus Julian algorithm. */
    private static LocalDate orthodoxEaster(int year) {
        int a = year % 4;
        int b = year % 7;
        int c = year % 19;
        int d = (19 * c + 15) % 30;
        int e = (2 * a + 4 * b - d + 34) % 7;
        int total = d + e + 114;
        int month = total / 31;               // 3 = March, 4 = April (Julian)
        int day = total % 31 + 1;
        int daysAfterJulianMarch1 = month == 3 ? day - 1 : 31 + (day - 1);
        return LocalDate.of(year, 3, 1).plusDays(daysAfterJulianMarch1 + 13L);
    }
}
