package com.ethiopica.ui;

import javax.swing.JButton;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Rounded "pill" button matching the prototype, custom-painted so it looks the
 * same under every platform look-and-feel.
 *
 * <p>Primary = filled emerald (the ዛሬ "today" button); secondary = white with an
 * emerald-on-hover outline (the ‹ ቀዳሚ / ቀጣይ › nav buttons).
 */
class PillButton extends JButton {

    private final boolean primary;

    PillButton(String text, boolean primary) {
        super(text);
        this.primary = primary;
        setFont(EthiopicFonts.sans(Font.BOLD, 13f));
        // Suppress the L&F's own button chrome; we paint the capsule ourselves.
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setRolloverEnabled(true);                                  // so getModel().isRollover() tracks hover
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(new EmptyBorder(9, 17, 9, 17));                  // padding around the label
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // arc == height yields fully rounded (capsule) ends.
        int w = getWidth(), h = getHeight(), arc = h;
        boolean hover = getModel().isRollover();

        // Primary: solid emerald, darkening on hover. Secondary: white that fills
        // emerald on hover (its outline, drawn below, also turns emerald).
        Color bg;
        if (primary)      bg = hover ? Theme.EMERALD_DARK : Theme.EMERALD;
        else              bg = hover ? Theme.EMERALD : Color.WHITE;

        g.setColor(bg);
        g.fillRoundRect(0, 0, w - 1, h - 1, arc, arc);

        if (!primary) {
            g.setColor(hover ? Theme.EMERALD : Theme.BTN_BORDER);
            g.drawRoundRect(0, 0, w - 1, h - 1, arc, arc);
        }

        // Label colour must contrast the fill: white on any emerald fill, emerald
        // on the secondary button's resting white.
        setForeground(primary ? Color.WHITE : (hover ? Color.WHITE : Theme.EMERALD));
        g.dispose();
        super.paintComponent(g0);   // let the L&F paint the text label on top of our background
    }
}
