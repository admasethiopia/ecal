package com.ethiopica.ui;

/**
 * Backend seam for {@link CalendarPanel}. Implement this once over your calendar
 * data; the panel uses it for every month switch, the month/year picker, and
 * keyboard navigation.
 *
 * <p>The panel never does calendar arithmetic itself — it asks the source to
 * build a month ({@link #forMonth}) or to resolve a relative day move
 * ({@link #resolve}). That keeps the UI free of any one calendar's rules.
 */
public interface CalendarSource {

    /**
     * Build the model for a month. Implementations must normalise out-of-range
     * input (e.g. month 0 → last month of the previous year) and clamp the year
     * to whatever range they support, so the panel can pass {@code month ± 1}
     * freely.
     */
    CalendarModel forMonth(int month, long year);

    /**
     * Resolve a day move of {@code deltaDays} from the given day, crossing month
     * and year boundaries as needed.
     *
     * @return {@code {month, year, day}} of the resulting day, clamped to the
     *         supported range.
     */
    long[] resolve(int month, long year, int day, int deltaDays);

    /** All month names in order, for the month picker. */
    String[] monthNames();

    /** Whether routine (everyday) commemorations are included in day details. */
    void setIncludeRoutine(boolean includeRoutine);

    /** Month (1-based) that today falls in. */
    int todayMonth();

    /** Year that today falls in. */
    long todayYear();

    /** Day-of-month that today falls on. */
    int todayDay();
}
