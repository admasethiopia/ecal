package org.ecal;

/**
 * A single commemoration on a given day.
 *
 * <p>{@code routine} marks the everyday monthly commemoration that every day
 * carries, distinguishing it from notable feasts and fasts. Consumers use this
 * flag instead of relying on the position of an event within a list.
 */
public record CalendarEvent(String category, String title, boolean routine) {
    static CalendarEvent routine(String category, String title) {
        return new CalendarEvent(category, title, true);
    }

    static CalendarEvent notable(String category, String title) {
        return new CalendarEvent(category, title, false);
    }
}
