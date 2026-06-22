package org.ecal;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.BorderFactory;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;

final class Theme {
    static final Color BACKGROUND = new Color(0xF7F5F0);
    static final Color SURFACE = Color.WHITE;
    static final Color TEXT = new Color(0x202124);
    static final Color MUTED_TEXT = new Color(0x5F6368);
    static final Color BORDER = new Color(0xDADCE0);
    static final Color ACCENT = new Color(0x2F6F73);
    static final Color ACCENT_SOFT = new Color(0xDCEBEC);
    static final Color TODAY = new Color(0xB3261E);
    static final Color EVENT = new Color(0xB78103);

    static final Border CELL_BORDER = BorderFactory.createLineBorder(BORDER);
    private static Font ethiopicBaseFont;

    private Theme() {
    }

    static void installLookAndFeel() {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ignored) {
            // Keep Swing's cross-platform look and feel if the system look and feel is unavailable.
        }
    }

    static Font uiFont(float size, int style) {
        Font font = UIManager.getFont("Label.font");
        if (font == null) {
            font = new Font(Font.SANS_SERIF, Font.PLAIN, Math.round(size));
        }
        return font.deriveFont(style, size);
    }

    static Font ethiopicFont(float size, int style) {
        Font font = ethiopicBaseFont();
        if (font != null) {
            return font.deriveFont(style, size);
        }
        return uiFont(size, style);
    }

    private static Font ethiopicBaseFont() {
        if (ethiopicBaseFont != null) {
            return ethiopicBaseFont;
        }

        ethiopicBaseFont = loadFont("/NotoSansEthiopic.ttf");
        if (ethiopicBaseFont != null) {
            return ethiopicBaseFont;
        }

        for (String family : new String[]{"Noto Sans Ethiopic", "Nyala", "Abyssinica SIL", "FreeSerif"}) {
            Font candidate = new Font(family, Font.PLAIN, 14);
            if (candidate.canDisplayUpTo("መስከረም ጥቅምት ፩፪፫") == -1) {
                ethiopicBaseFont = candidate;
                return ethiopicBaseFont;
            }
        }

        ethiopicBaseFont = uiFont(14f, Font.PLAIN);
        return ethiopicBaseFont;
    }

    private static Font loadFont(String resource) {
        try (InputStream stream = Theme.class.getResourceAsStream(resource)) {
            if (stream != null) {
                return Font.createFont(Font.TRUETYPE_FONT, stream);
            }
        } catch (FontFormatException | IOException ignored) {
            // Fall through to system fonts.
        }
        return null;
    }
}
