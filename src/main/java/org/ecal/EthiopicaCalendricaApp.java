package org.ecal;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public final class EthiopicaCalendricaApp {
    private EthiopicaCalendricaApp() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Theme.installLookAndFeel();
            JFrame frame = new JFrame("Ethiopica Calendrica");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());
            frame.add(new EthiopianCalendarPanel(), BorderLayout.CENTER);
            frame.setMinimumSize(new Dimension(980, 680));
            frame.setPreferredSize(new Dimension(1060, 740));
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
