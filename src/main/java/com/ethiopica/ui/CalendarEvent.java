package com.ethiopica.ui;

/**
 * A single entry shown in the day-detail panel.
 *
 * @param category Amharic category label, e.g. "ዓመታዊ በዓል" (annual feast),
 *                 "ወርሃዊ በዓል" (monthly commemoration), or "ጾም" (fast).
 * @param name     the feast/fast name, e.g. "ግዝረት".
 */
public record CalendarEvent(String category, String name) {}
