package com.ethiopica.ui;

import java.util.List;

/**
 * Everything {@link CalendarPanel} needs to render ONE Ethiopian month.
 *
 * Implement this against your existing backend (see the {@code org.ecal}
 * adapter).
 *
 * Month switching, the month/year picker, and keyboard navigation are driven by
 * a {@link CalendarSource}, which builds a fresh CalendarModel per month.
 */
public interface CalendarModel {

    /** Month number, 1-based (1 … 13). */
    int monthNumber();

    /** Year number (e.g. 2018 E.C.). */
    long yearNumber();

    /** Amharic month name, e.g. "ጥር". */
    String amharicMonthName();

    /** Amharic year label, e.g. "2018 ዓ.ም". */
    String amharicYearLabel();

    /** Ge'ez year label used in the detail header, e.g. "፳፻፲፰ ዓ.ም". */
    String geezYearLabel();

    /** Gregorian span label, e.g. "Jan 9 – Feb 7, 2026". */
    String gregorianRangeLabel();

    /** Seven weekday headers, Sunday-first, e.g. {"እሑድ", "ሰኞ", ...}. */
    String[] weekdayNames();

    /** Weekday column (0 = Sunday … 6 = Saturday) on which day #1 falls. */
    int firstDayWeekIndex();

    /** All days of the month, in order (day 1 … N). */
    List<DayCell> days();

    /** Events for a given day; return an empty list (never null) if none. */
    List<CalendarEvent> eventsFor(DayCell day);

    /** Day-of-month that "today" lands on within this month, or -1 if today is in another month. */
    int todayDayOfMonth();
}
