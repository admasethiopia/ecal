package com.ethiopica.ui;

import javax.swing.JComponent;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

/**
 * A single custom-painted calendar cell: background by state, big day numeral
 * (Ge'ez or Western), small Gregorian corner label, and a feast dot. A null
 * {@link DayCell} renders an inert leading/trailing blank.
 */
class DayCellComponent extends JComponent {

    private static final int PREF_W = 150;
    private static final int PREF_H = 106;

    private final DayCell day;          // null => blank cell
    private final boolean showGregorian;
    private final boolean showFasting;
    private final boolean showHoliday;
    private final boolean showGeez;
    private final float numeralSize;

    private boolean selected;
    private boolean hover;

    DayCellComponent(DayCell day,
                     boolean showGregorian, boolean showFasting, boolean showHoliday,
                     boolean showGeez, float numeralSize,
                     Consumer<DayCell> onSelect) {
        this.day = day;
        this.showGregorian = showGregorian;
        this.showFasting = showFasting;
        this.showHoliday = showHoliday;
        this.showGeez = showGeez;
        this.numeralSize = numeralSize;
        setPreferredSize(new Dimension(PREF_W, PREF_H));

        if (day != null) {
            setFocusable(true);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                @Override public void mouseExited(MouseEvent e)  { hover = false; repaint(); }
                @Override public void mousePressed(MouseEvent e) {
                    requestFocusInWindow();
                    if (onSelect != null) onSelect.accept(day);
                }
            });
        }
    }

    DayCell day() { return day; }

    void setSelected(boolean s) {
        if (s != selected) { selected = s; repaint(); }
    }

    private boolean isFasting() { return day != null && day.fasting() && showFasting; }
    private boolean isHoliday() { return day != null && day.holiday() && showHoliday; }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();

        // --- background ---
        Color bg;
        if (day == null)        bg = Theme.BLANK_BG;
        else if (selected)      bg = Theme.SELECTED_BG;
        else if (isFasting())   bg = hover ? shade(Theme.FASTING_BG, -6) : Theme.FASTING_BG;
        else                    bg = hover ? Theme.HOVER_BG : Theme.CARD_BG;
        g.setColor(bg);
        g.fillRect(0, 0, w, h);

        // --- right + bottom grid hairlines ---
        g.setColor(Theme.GRID_LINE);
        g.drawLine(w - 1, 0, w - 1, h);
        g.drawLine(0, h - 1, w, h - 1);

        if (day == null) { g.dispose(); return; }

        // --- Gregorian corner label ---
        if (showGregorian) {
            g.setFont(EthiopicFonts.sans(Font.PLAIN, 11f));
            g.setColor(selected ? Theme.SELECTED_GREG : Theme.GREG_MUTED);
            FontMetrics fm = g.getFontMetrics();
            String gd = day.gregShort();
            g.drawString(gd, w - 12 - fm.stringWidth(gd), 9 + fm.getAscent());
        }

        // --- day numeral (centered) ---
        // The serif face carries no Latin glyphs, so Western digits must use the
        // sans face; Ge'ez numerals keep the serif look.
        String num = showGeez ? day.geez() : Integer.toString(day.dayOfMonth());
        g.setFont(showGeez
                ? EthiopicFonts.serif(Font.BOLD, numeralSize)
                : EthiopicFonts.sans(Font.BOLD, numeralSize));
        g.setColor(selected ? Theme.SELECTED_TEXT : Theme.INK);
        FontMetrics fm = g.getFontMetrics();
        int nx = (w - fm.stringWidth(num)) / 2;
        int ny = (h - fm.getHeight()) / 2 + fm.getAscent();
        g.drawString(num, nx, ny);

        // --- feast dot ---
        if (isHoliday()) {
            g.setColor(selected ? Theme.SELECTED_DOT : Theme.HOLIDAY_DOT);
            int d = 6;
            g.fillOval(w / 2 - d / 2, h - 13 - d, d, d);
        }

        g.dispose();
    }

    private static Color shade(Color c, int delta) {
        return new Color(
                clamp(c.getRed() + delta),
                clamp(c.getGreen() + delta),
                clamp(c.getBlue() + delta));
    }

    private static int clamp(int v) { return Math.max(0, Math.min(255, v)); }
}
