package com.ethiopica.ui;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.InputStream;

/**
 * Loads and caches the bundled Ethiopic fonts via {@link Font#createFont}.
 *
 * Place the TrueType files on the classpath root under /fonts/ :
 *   /fonts/NotoSerifEthiopic-Regular.ttf
 *   /fonts/NotoSansEthiopic-Regular.ttf
 *
 * Download from Google Fonts (Noto Serif Ethiopic, Noto Sans Ethiopic).
 * If a file is missing the loader logs a warning and falls back to a logical
 * font so the app still runs (Ge'ez glyphs then depend on an OS Ethiopic font).
 *
 * Both Noto families include the basic Latin set, so the same faces render the
 * Gregorian date strings too. If Latin shows as tofu boxes, your build is using
 * a fallback font without Latin — install the Noto TTFs as above.
 */
public final class EthiopicFonts {
    private EthiopicFonts() {}

    private static Font serifBase;
    private static Font sansBase;
    private static boolean loaded = false;

    public static synchronized void load() {
        if (loaded) return;
        serifBase = loadFont("/fonts/NotoSerifEthiopic-Regular.ttf", Font.SERIF);
        sansBase  = loadFont("/fonts/NotoSansEthiopic-Regular.ttf", Font.SANS_SERIF);
        loaded = true;
    }

    private static Font loadFont(String resourcePath, String fallbackFamily) {
        try (InputStream in = EthiopicFonts.class.getResourceAsStream(resourcePath)) {
            if (in != null) {
                Font f = Font.createFont(Font.TRUETYPE_FONT, in);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(f);
                return f.deriveFont(Font.PLAIN, 14f);
            }
            System.err.println("EthiopicFonts: resource not found " + resourcePath
                    + " — falling back to '" + fallbackFamily + "'.");
        } catch (Exception e) {
            System.err.println("EthiopicFonts: failed to load " + resourcePath
                    + " (" + e.getMessage() + ") — falling back to '" + fallbackFamily + "'.");
        }
        return new Font(fallbackFamily, Font.PLAIN, 14);
    }

    /** Serif (Noto Serif Ethiopic) — used for Ge'ez numerals and the month title. */
    public static Font serif(int style, float size) {
        load();
        return serifBase.deriveFont(style, size);
    }

    /** Sans (Noto Sans Ethiopic) — used for UI labels and Amharic body text. */
    public static Font sans(int style, float size) {
        load();
        return sansBase.deriveFont(style, size);
    }
}
