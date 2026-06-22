package org.ecal;

import com.formdev.flatlaf.FlatLightLaf;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.BorderFactory;
import javax.swing.UIManager;
import javax.swing.border.Border;

final class Theme {
    static final Color BACKGROUND = new Color(0xF7F5F0);
    static final Color SURFACE = Color.WHITE;
    static final Color SURFACE_MUTED = new Color(0xF1EFEA);
    static final Color TEXT = new Color(0x202124);
    static final Color MUTED_TEXT = new Color(0x5F6368);
    static final Color BORDER = new Color(0xDADCE0);
    static final Color ACCENT = new Color(0x2F6F73);
    static final Color ACCENT_SOFT = new Color(0xDCEBEC);
    static final Color TODAY = new Color(0xB3261E);
    static final Color TODAY_SOFT = new Color(0xFCEEEE);
    static final Color EVENT = new Color(0xB78103);
    static final Color EVENT_SOFT = new Color(0xFFF8E1);

    static final Border CELL_BORDER = BorderFactory.createLineBorder(BORDER);
    private static Font ethiopicBaseFont;

    private Theme() {
    }

    static void installLookAndFeel() {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        // A FlatLaf failure must never block startup; fall back to the default L&F.
        if (!FlatLightLaf.setup()) {
            return;
        }
        // Tuned defaults for a softer, modern flat look.
        UIManager.put("Component.accentColor", ACCENT);
        UIManager.put("Button.arc", 999);
        UIManager.put("Component.arc", 12);
        UIManager.put("Component.focusWidth", 1);
        UIManager.put("ScrollBar.width", 12);
        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("ToolBar.separatorColor", BORDER);
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
