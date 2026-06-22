package org.ecal;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.BorderFactory;
import javax.swing.JButton;

final class CalendarDayButton extends JButton {
    private static final DateTimeFormatter GREGORIAN_FORMAT = DateTimeFormatter.ofPattern("MMM d");

    /** The visual state of a cell. All cell colours derive from this in one place. */
    private enum DayState { EMPTY, NORMAL, EVENT, TODAY, SELECTED }

    private record Style(Color background, Color border, Color mainText, Color subText, boolean ring) {
    }

    private EthiopianDate date;
    private String ethiopianText = "";
    private String gregorianText = "";
    private int eventCount;
    private DayState state = DayState.EMPTY;

    CalendarDayButton() {
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(116, 78));
        setMinimumSize(new Dimension(72, 56));
    }

    void setDate(
        EthiopianDate date,
        boolean selected,
        boolean today,
        boolean showEthiopicNumbers,
        boolean showGregorian,
        boolean showAllEvents,
        float dayFontSize
    ) {
        this.date = date;
        if (date == null) {
            this.state = DayState.EMPTY;
            this.ethiopianText = "";
            this.gregorianText = "";
            this.eventCount = 0;
            setEnabled(false);
            repaint();
            return;
        }

        setEnabled(true);
        this.ethiopianText = showEthiopicNumbers ? EthiopicNumerals.format(date.day()) : Integer.toString(date.day());
        LocalDate gregorian = date.toGregorian();
        this.gregorianText = showGregorian ? GREGORIAN_FORMAT.format(gregorian) : "";
        int notableCount = EventData.notableEventsFor(date).size();
        this.eventCount = showAllEvents ? Math.min(notableCount, 4) : 0;
        this.state = selected ? DayState.SELECTED
            : today ? DayState.TODAY
            : eventCount > 0 ? DayState.EVENT
            : DayState.NORMAL;
        setFont(Theme.ethiopicFont(dayFontSize, Font.PLAIN));
        repaint();
    }

    EthiopianDate date() {
        return date;
    }

    private static Style styleFor(DayState state) {
        return switch (state) {
            case EMPTY -> new Style(Theme.SURFACE_MUTED, Theme.BORDER, Theme.MUTED_TEXT, Theme.MUTED_TEXT, false);
            case NORMAL -> new Style(Theme.SURFACE, Theme.BORDER, Theme.TEXT, Theme.MUTED_TEXT, false);
            case EVENT -> new Style(Theme.EVENT_SOFT, Theme.BORDER, Theme.TEXT, Theme.MUTED_TEXT, false);
            case TODAY -> new Style(Theme.TODAY_SOFT, Theme.TODAY, Theme.TODAY, Theme.MUTED_TEXT, true);
            case SELECTED -> new Style(Theme.ACCENT, Theme.ACCENT.darker(), Color.WHITE, new Color(0xEAF2F2), false);
        };
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            Style style = styleFor(state);
            int width = getWidth();
            int height = getHeight();

            g.setColor(style.background());
            g.fillRect(0, 0, width, height);
            g.setColor(style.border());
            g.drawRect(0, 0, width - 1, height - 1);

            if (date == null) {
                return;
            }

            g.setFont(Theme.uiFont(10.5f, Font.PLAIN));
            g.setColor(style.subText());
            FontMetrics smallMetrics = g.getFontMetrics();
            g.drawString(gregorianText, width - smallMetrics.stringWidth(gregorianText) - 8, 15);

            Font dayFont = getFont();
            g.setFont(dayFont);
            g.setColor(style.mainText());
            FontMetrics dayMetrics = g.getFontMetrics();
            int dayX = (width - dayMetrics.stringWidth(ethiopianText)) / 2;
            int dayY = Math.round(height * 0.54f + dayMetrics.getAscent() / 2f) - 6;
            g.drawString(ethiopianText, dayX, dayY);

            if (eventCount > 0) {
                int dotDiameter = 5;
                int gap = 4;
                int totalWidth = eventCount * dotDiameter + (eventCount - 1) * gap;
                int startX = (width - totalWidth) / 2;
                int y = height - 12;
                for (int i = 0; i < eventCount; i++) {
                    g.setColor(i == 0 ? Theme.EVENT : Theme.ACCENT);
                    g.fillOval(startX + i * (dotDiameter + gap), y, dotDiameter, dotDiameter);
                }
            }

            if (style.ring()) {
                g.setColor(style.border());
                g.setStroke(new BasicStroke(1.6f));
                g.drawRoundRect(5, 5, width - 11, height - 11, 10, 10);
            }
        } finally {
            g.dispose();
        }
    }
}
