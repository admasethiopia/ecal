package org.ecal;

import com.ethiopica.ui.CalendarPanel;
import com.ethiopica.ui.EthiopicFonts;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

public final class EthiopicaCalendricaApp {
    private EthiopicaCalendricaApp() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            System.setProperty("awt.useSystemAAFontSettings", "on");
            System.setProperty("swing.aatext", "true");
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Any L&F failure must never block startup; fall back to the default.
            }
            EthiopicFonts.load();

            CalendarPanel calendar = new CalendarPanel(new EcalCalendarSource());

            JFrame frame = new JFrame("Ethiopica Calendrica");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setContentPane(calendar);
            // Size the window to the calendar itself: it loads fully visible, with
            // no outer margin and no scrollbars. The layout has a constant height,
            // so pinning the minimum to the packed size keeps the calendar from
            // ever being squished.
            frame.pack();
            frame.setMinimumSize(frame.getSize());
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
