package org.ecal;

import com.ethiopica.ui.CalendarModel;
import com.ethiopica.ui.CalendarSource;
import com.ethiopica.ui.DayCell;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Adapts the {@code org.ecal} calendar domain ({@link EthiopianDate} /
 * {@link EventData}) to the UI's {@link CalendarSource} / {@link CalendarModel}
 * seam. This is the single bridge between the backend and the Swing widget.
 */
public final class EcalCalendarSource implements CalendarSource {

    private static final int MONTHS = EthiopianDate.MONTH_NAMES.length; // 13
    private static final int MIN_YEAR = 1;
    private static final int MAX_YEAR = 9999;

    private final EthiopianDate today;
    private boolean includeRoutine = true;

    public EcalCalendarSource() {
        this.today = EthiopianDate.fromGregorian(LocalDate.now());
    }

    @Override
    public CalendarModel forMonth(int month, long year) {
        // Normalise an out-of-range month into the adjacent year.
        while (month < 1) { month += MONTHS; year--; }
        while (month > MONTHS) { month -= MONTHS; year++; }
        year = Math.max(MIN_YEAR, Math.min(MAX_YEAR, year));
        return new MonthModel(month, year, includeRoutine, today);
    }

    @Override
    public long[] resolve(int month, long year, int day, int deltaDays) {
        EthiopianDate from = new EthiopianDate(day, month, year);
        EthiopianDate to = EthiopianDate.fromFixedDay(from.toFixedDay() + deltaDays);
        if (to.year() < MIN_YEAR || to.year() > MAX_YEAR) {
            return new long[] {month, year, day};
        }
        return new long[] {to.month(), to.year(), to.day()};
    }

    @Override
    public String[] monthNames() {
        return EthiopianDate.MONTH_NAMES.clone();
    }

    @Override
    public void setIncludeRoutine(boolean includeRoutine) {
        this.includeRoutine = includeRoutine;
    }

    @Override public int  todayMonth() { return today.month(); }
    @Override public long todayYear()  { return today.year(); }
    @Override public int  todayDay()   { return today.day(); }

    /** One Ethiopian month rendered as a {@link CalendarModel}. */
    private static final class MonthModel implements CalendarModel {
        private static final DateTimeFormatter SPAN_START =
                DateTimeFormatter.ofPattern("MMM d", Locale.ENGLISH);
        private static final DateTimeFormatter SPAN_END =
                DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);

        private final int month;
        private final long year;
        private final boolean includeRoutine;
        private final EthiopianDate today;
        private final List<DayCell> days;

        MonthModel(int month, long year, boolean includeRoutine, EthiopianDate today) {
            this.month = month;
            this.year = year;
            this.includeRoutine = includeRoutine;
            this.today = today;

            int length = EthiopianDate.lengthOfMonth(month, year);
            List<DayCell> built = new ArrayList<>(length);
            for (int d = 1; d <= length; d++) {
                EthiopianDate date = new EthiopianDate(d, month, year);
                built.add(new DayCell(
                        d,
                        date.toGregorian(),
                        isFasting(date),
                        EventData.hasNotableEvents(date)));
            }
            this.days = List.copyOf(built);
        }

        @Override public int  monthNumber() { return month; }
        @Override public long yearNumber()  { return year; }

        @Override public String amharicMonthName() { return EthiopianDate.MONTH_NAMES[month - 1]; }
        @Override public String amharicYearLabel() { return year + " ዓ.ም"; }
        @Override public String geezYearLabel()    { return EthiopicNumerals.format(year) + " ዓ.ም"; }

        @Override
        public String gregorianRangeLabel() {
            LocalDate first = new EthiopianDate(1, month, year).toGregorian();
            LocalDate last = new EthiopianDate(EthiopianDate.lengthOfMonth(month, year), month, year).toGregorian();
            return SPAN_START.format(first) + " – " + SPAN_END.format(last);
        }

        @Override public String[] weekdayNames() { return EthiopianDate.WEEKDAY_NAMES.clone(); }

        @Override public int firstDayWeekIndex() { return new EthiopianDate(1, month, year).dayOfWeek(); }

        @Override public List<DayCell> days() { return days; }

        @Override
        public List<com.ethiopica.ui.CalendarEvent> eventsFor(DayCell day) {
            EthiopianDate date = new EthiopianDate(day.dayOfMonth(), month, year);
            List<com.ethiopica.ui.CalendarEvent> out = new ArrayList<>();
            for (CalendarEvent e : EventData.eventsFor(date)) {
                if (!includeRoutine && e.routine()) {
                    continue;
                }
                out.add(new com.ethiopica.ui.CalendarEvent(e.category(), e.title()));
            }
            return out;
        }

        @Override
        public int todayDayOfMonth() {
            return today.month() == month && today.year() == year ? today.day() : -1;
        }

        /**
         * A day counts as fasting if it carries a dedicated fast (category exactly
         * "ጾም") or an event whose name marks it as one ("ጾም"/"ጾመ …"). We match the
         * category exactly rather than by substring: the movable category
         * "ተንቀሳቃሽ በዓል/ጾም" covers both feasts and fasts, so a substring test would
         * wrongly tint feasts like ትንሳኤ (Easter) and ሆሳእና (Palm Sunday).
         */
        private static boolean isFasting(EthiopianDate date) {
            for (CalendarEvent e : EventData.eventsFor(date)) {
                if (e.category().equals("ጾም")) {
                    return true;
                }
                if (e.title().contains("ጾም") || e.title().contains("ጾመ")) {
                    return true;
                }
            }
            return false;
        }
    }
}
