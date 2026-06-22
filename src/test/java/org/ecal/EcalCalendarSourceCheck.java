package org.ecal;

import com.ethiopica.ui.CalendarModel;
import com.ethiopica.ui.CalendarSource;
import com.ethiopica.ui.DayCell;
import java.util.Map;

/**
 * Verifies {@link EcalCalendarSource}, the adapter that bridges the calendar
 * domain ({@link EthiopianDate} / {@link EventData}) to the Swing UI's
 * {@link CalendarSource} / {@link CalendarModel} seam.
 *
 * <p>These checks pin the behaviour the UI relies on: month normalisation and
 * year clamping (so the panel can pass {@code month ± 1} blindly), relative-day
 * resolution across month/year boundaries (arrow-key navigation), the
 * feast/fast classification driving the grid's dot and tint, and the
 * routine-event toggle.
 */
public final class EcalCalendarSourceCheck {
    private static final String EASTER = "ትንሳኤ";

    private EcalCalendarSourceCheck() {
    }

    public static void main(String[] args) {
        run();
    }

    static void run() {
        checkMonthNormalisation();
        checkYearClamping();
        checkMonthShape();
        checkDayResolution();
        checkFeastAndFastClassification();
        checkRoutineToggle();
        System.out.println("Ecal calendar source checks passed.");
    }

    /** Out-of-range months must roll into the adjacent year so prev/next "just work". */
    private static void checkMonthNormalisation() {
        CalendarSource source = new EcalCalendarSource();

        // Month 0 → ጳጉሜን (13) of the previous year.
        CalendarModel before = source.forMonth(0, 2018);
        expect(before.monthNumber() == 13 && before.yearNumber() == 2017,
            "forMonth(0, 2018) should normalise to 13/2017, got "
                + before.monthNumber() + "/" + before.yearNumber());

        // Month 14 → መስከረም (1) of the next year.
        CalendarModel after = source.forMonth(14, 2018);
        expect(after.monthNumber() == 1 && after.yearNumber() == 2019,
            "forMonth(14, 2018) should normalise to 1/2019, got "
                + after.monthNumber() + "/" + after.yearNumber());
    }

    /** The year is clamped to the supported range so the picker can never overshoot. */
    private static void checkYearClamping() {
        CalendarSource source = new EcalCalendarSource();
        expect(source.forMonth(1, 0).yearNumber() == 1, "year below range should clamp to 1");
        expect(source.forMonth(1, 100000).yearNumber() == 9999, "year above range should clamp to 9999");
    }

    /** Day counts, weekday offset, and ጳጉሜን's leap-sensitive length must match the domain. */
    private static void checkMonthShape() {
        CalendarSource source = new EcalCalendarSource();

        CalendarModel meskerem = source.forMonth(1, 2018);
        expect(meskerem.days().size() == 30, "መስከረም should have 30 days");
        expect(meskerem.firstDayWeekIndex() == new EthiopianDate(1, 1, 2018).dayOfWeek(),
            "firstDayWeekIndex must match the domain weekday of day 1");

        // 2018 is not a leap year (2018 % 4 != 3) → ጳጉሜን has 5 days.
        expect(source.forMonth(13, 2018).days().size() == 5, "ጳጉሜን 2018 should have 5 days");
        // 2019 is a leap year (2019 % 4 == 3) → ጳጉሜን has 6 days.
        expect(source.forMonth(13, 2019).days().size() == 6, "ጳጉሜን 2019 should have 6 days");
    }

    /** Relative-day moves must cross month and year boundaries and clamp at the range edge. */
    private static void checkDayResolution() {
        CalendarSource source = new EcalCalendarSource();

        // Last day of መስከረም + 1 → first day of ጥቅምት.
        long[] forward = source.resolve(1, 2018, 30, 1);
        expect(forward[0] == 2 && forward[1] == 2018 && forward[2] == 1,
            "30/1/2018 + 1 day should be 1/2/2018");

        // First day of መስከረም − 1 → last day of the previous ጳጉሜን (2017: 5 days).
        long[] backward = source.resolve(1, 2018, 1, -1);
        expect(backward[0] == 13 && backward[1] == 2017 && backward[2] == 5,
            "1/1/2018 − 1 day should be 5/13/2017");

        // Stepping before the first supported year is refused (returns the input unchanged).
        long[] pinned = source.resolve(1, 1, 1, -1);
        expect(pinned[0] == 1 && pinned[1] == 1 && pinned[2] == 1,
            "stepping below year 1 should leave the date unchanged");
    }

    private static void checkFeastAndFastClassification() {
        CalendarSource source = new EcalCalendarSource();

        // The Ethiopian new year carries notable feasts → it shows the holiday dot.
        DayCell newYear = source.forMonth(1, 2018).days().get(0);
        expect(newYear.holiday(), "1/1 should be flagged as a holiday");

        // ጾመ ገሃድ (10/5, category "ጾም") is a dedicated fast → fasting tint.
        DayCell gahad = source.forMonth(5, 2018).days().get(9); // day 10
        expect(gahad.fasting(), "10/5 (ጾመ ገሃድ) should be flagged as fasting");

        // Easter (ትንሳኤ) is a feast, not a fast — even though its category string
        // ("ተንቀሳቃሽ በዓል/ጾም") contains the word ጾም. This guards the substring bug.
        EthiopianDate easter = findMovableFeast(2018, EASTER);
        expect(easter != null, "Easter should be computed for 2018");
        DayCell easterCell = source.forMonth(easter.month(), easter.year()).days().get(easter.day() - 1);
        expect(!easterCell.fasting(), "Easter must NOT be flagged as fasting");
        expect(easterCell.holiday(), "Easter should be flagged as a holiday");
    }

    /** The "all events" toggle controls whether the everyday commemoration is listed. */
    private static void checkRoutineToggle() {
        EcalCalendarSource source = new EcalCalendarSource();

        // A plain day carries only the routine monthly commemoration.
        DayCell plainDay = dayOf(source.forMonth(1, 2018), 8);

        source.setIncludeRoutine(true);
        expect(source.forMonth(1, 2018).eventsFor(plainDay).size() == 1,
            "with routine included, 8/1 should list its one commemoration");

        source.setIncludeRoutine(false);
        expect(source.forMonth(1, 2018).eventsFor(plainDay).isEmpty(),
            "with routine excluded, 8/1 should list nothing");
    }

    private static DayCell dayOf(CalendarModel model, int dayOfMonth) {
        for (DayCell d : model.days()) {
            if (d.dayOfMonth() == dayOfMonth) {
                return d;
            }
        }
        throw new AssertionError("Day " + dayOfMonth + " not found in month");
    }

    private static EthiopianDate findMovableFeast(long ethiopianYear, String name) {
        for (Map.Entry<String, String> entry : EventData.movableEvents(ethiopianYear).entrySet()) {
            if (entry.getValue().equals(name)) {
                String[] parts = entry.getKey().split("/");
                return new EthiopianDate(Integer.parseInt(parts[1]), Integer.parseInt(parts[0]), ethiopianYear);
            }
        }
        return null;
    }

    private static void expect(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
