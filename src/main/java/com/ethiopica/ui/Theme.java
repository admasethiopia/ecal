package com.ethiopica.ui;

import java.awt.Color;

/**
 * Centralised colour palette for the "Refined Classic" calendar UI.
 *
 * <p>Every colour the widgets paint comes from here, so the look can be retuned
 * in one place. Values mirror the approved HTML prototype 1:1. The overall feel
 * is a warm cream "paper" card with a deep emerald accent and gold/red feast
 * markers.
 */
public final class Theme {
    private Theme() {}

    // Surfaces: the cream card the calendar sits on, and the slightly darker
    // backdrop the window fills behind it.
    public static final Color CARD_BG            = new Color(0xFF, 0xFD, 0xF8);
    public static final Color APP_BG             = new Color(0xE7, 0xE5, 0xDF);

    // Primary accent: emerald for the title, headers, selection, and pill buttons.
    public static final Color EMERALD            = new Color(0x1B, 0x5E, 0x4F);
    public static final Color EMERALD_DARK       = new Color(0x16, 0x49, 0x3E); // button hover

    // Secondary text tones, warm grey-browns of decreasing emphasis.
    public static final Color MUTED              = new Color(0x9C, 0x8F, 0x76); // subtitle / am-date
    public static final Color GREG_MUTED         = new Color(0xB0, 0xA1, 0x87); // Gregorian labels
    public static final Color LEGEND_TEXT        = new Color(0x8A, 0x7D, 0x65); // legend / toggles

    // Structural lines and the weekday header strip.
    public static final Color WEEKDAY_BG         = new Color(0xF3, 0xEF, 0xE4);
    public static final Color BORDER             = new Color(0xE7, 0xE0, 0xD0); // card / section edges
    public static final Color GRID_LINE          = new Color(0xEF, 0xE9, 0xDA); // hairlines between cells

    // Day-cell state backgrounds and the feast dot.
    public static final Color FASTING_BG         = new Color(0xFB, 0xF3, 0xDC); // gold tint on fast days
    public static final Color HOLIDAY_DOT        = new Color(0x9E, 0x2B, 0x25); // red feast marker
    public static final Color BLANK_BG           = new Color(0xFA, 0xF8, 0xF2); // leading/trailing empty cells
    public static final Color HOVER_BG           = new Color(0xF6, 0xF2, 0xE9); // mouse-over tint

    // The selected day: emerald fill with light text and a gold dot.
    public static final Color SELECTED_BG        = EMERALD;
    public static final Color SELECTED_TEXT      = new Color(0xF7, 0xF2, 0xE7);
    public static final Color SELECTED_GREG      = new Color(0xBC, 0xD4, 0xCA); // Gregorian label on selection
    public static final Color SELECTED_DOT       = new Color(0xE8, 0xC5, 0x5C); // feast dot on selection

    // Numerals and the detail panel's category/value/empty-state text.
    public static final Color INK                = new Color(0x2B, 0x26, 0x20); // day numerals
    public static final Color DETAIL_CAT         = new Color(0xA8, 0x98, 0x78); // event category column
    public static final Color DETAIL_VAL         = new Color(0x3A, 0x2F, 0x24); // event name column
    public static final Color DETAIL_NONE        = new Color(0xB5, 0xA8, 0x8F); // "no events" placeholder

    // Secondary (outline) pill button border, and the legend's fasting swatch outline.
    public static final Color BTN_BORDER         = new Color(0xD4, 0xDD, 0xD6);
    public static final Color CHIP_SWATCH_BORDER = new Color(0xE6, 0xD2, 0x9B);
}
