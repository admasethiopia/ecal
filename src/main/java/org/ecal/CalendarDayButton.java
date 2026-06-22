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

    private EthiopianDate date;
    private String ethiopianText = "";
    private String gregorianText = "";
    private int eventCount;
    private boolean selected;
    private boolean today;

    CalendarDayButton() {
        setBorder(Theme.CELL_BORDER);
        setBackground(Theme.SURFACE);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(104, 70));
        setMinimumSize(new Dimension(88, 62));
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
        this.selected = selected;
        this.today = today;
        if (date == null) {
            setEnabled(false);
            this.ethiopianText = "";
            this.gregorianText = "";
            this.eventCount = 0;
            setBackground(new Color(0xF1F1F1));
            setBorder(Theme.CELL_BORDER);
            repaint();
            return;
        }

        setEnabled(true);
        this.ethiopianText = showEthiopicNumbers ? EthiopicNumerals.format(date.day()) : Integer.toString(date.day());
        LocalDate gregorian = date.toGregorian();
        this.gregorianText = showGregorian ? GREGORIAN_FORMAT.format(gregorian) : "";
        int notableCount = EventData.notableEventsFor(date).size();
        this.eventCount = showAllEvents ? Math.min(notableCount, 4) : 0;
        setFont(Theme.ethiopicFont(dayFontSize, Font.PLAIN));

        if (selected) {
            setBackground(Theme.ACCENT);
            setBorder(BorderFactory.createLineBorder(Theme.ACCENT.darker(), 2));
        } else if (today) {
            setBackground(new Color(0xFCEEEE));
            setBorder(BorderFactory.createLineBorder(Theme.TODAY, 2));
        } else if (eventCount > 0) {
            setBackground(new Color(0xFFF8E1));
            setBorder(Theme.CELL_BORDER);
        } else {
            setBackground(Theme.SURFACE);
            setBorder(Theme.CELL_BORDER);
        }
        repaint();
    }

    EthiopianDate date() {
        return date;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());

            if (date == null) {
                g.setColor(new Color(0xECECEC));
                g.drawLine(0, 0, getWidth(), 0);
                return;
            }

            int width = getWidth();
            int height = getHeight();
            Color mainText = selected ? Color.WHITE : today ? Theme.TODAY : Theme.TEXT;
            Color subtleText = selected ? new Color(0xEAF2F2) : Theme.MUTED_TEXT;

            Font gregorianFont = Theme.uiFont(10.5f, Font.PLAIN);
            g.setFont(gregorianFont);
            g.setColor(subtleText);
            FontMetrics smallMetrics = g.getFontMetrics();
            int smallY = 14;
            g.drawString(gregorianText, width - smallMetrics.stringWidth(gregorianText) - 8, smallY);

            Font dayFont = getFont();
            g.setFont(dayFont);
            g.setColor(mainText);
            FontMetrics dayMetrics = g.getFontMetrics();
            int dayX = (width - dayMetrics.stringWidth(ethiopianText)) / 2;
            int dayY = Math.round(height * 0.54f + dayMetrics.getAscent() / 2f) - 6;
            g.drawString(ethiopianText, dayX, dayY);

            if (eventCount > 0) {
                int dotDiameter = 4;
                int gap = 4;
                int totalWidth = eventCount * dotDiameter + (eventCount - 1) * gap;
                int startX = (width - totalWidth) / 2;
                int y = height - 11;
                for (int i = 0; i < eventCount; i++) {
                    g.setColor(i == 0 ? Theme.EVENT : Theme.ACCENT);
                    g.fillOval(startX + i * (dotDiameter + gap), y, dotDiameter, dotDiameter);
                }
            }
        } finally {
            g.dispose();
        }
    }

    @Override
    protected void paintBorder(Graphics graphics) {
        super.paintBorder(graphics);
        if (today && !selected) {
            Graphics2D g = (Graphics2D) graphics.create();
            try {
                g.setColor(Theme.TODAY);
                g.setStroke(new BasicStroke(1.4f));
                g.drawRoundRect(5, 5, getWidth() - 11, getHeight() - 11, 8, 8);
            } finally {
                g.dispose();
            }
        }
    }
}
