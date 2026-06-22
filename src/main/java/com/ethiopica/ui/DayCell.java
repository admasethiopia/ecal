package com.ethiopica.ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * One day of an Ethiopian month.
 *
 * @param dayOfMonth 1-based Ethiopian day (1–30, or 1–6 for ጳጉሜ).
 * @param gregorian  the corresponding Gregorian date.
 * @param fasting    true if it is a fasting day (tinted gold in the grid).
 * @param holiday    true if a feast falls on this day (red dot in the grid).
 */
public record DayCell(int dayOfMonth, LocalDate gregorian, boolean fasting, boolean holiday) {

    private static final DateTimeFormatter SHORT =
            DateTimeFormatter.ofPattern("MMM d", Locale.ENGLISH);
    private static final DateTimeFormatter FULL =
            DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.ENGLISH);

    /** Ge'ez numeral for the day, e.g. 6 → "፮". */
    public String geez() {
        return GeezNumerals.toGeez(dayOfMonth);
    }

    /** Short Gregorian label shown in the cell corner, e.g. "Jan 14". */
    public String gregShort() {
        return gregorian.format(SHORT);
    }

    /** Full Gregorian label shown in the detail panel, e.g. "Wednesday, January 14, 2026". */
    public String gregFull() {
        return gregorian.format(FULL);
    }
}
