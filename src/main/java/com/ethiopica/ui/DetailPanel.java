package com.ethiopica.ui;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;

/**
 * Bottom panel describing the selected day: Amharic weekday + date, the
 * Gregorian date, and a list of feast/fast rows (category | name).
 */
class DetailPanel extends JPanel {

    private static final int EVENT_ROW_HEIGHT = 48;

    private final JLabel weekday = new JLabel();
    private final JLabel amDate  = new JLabel();
    private final JLabel gregFull = new JLabel();
    private final JPanel rows = new JPanel();

    DetailPanel() {
        setBackground(Theme.CARD_BG);
        setBorder(new CompoundBorder(
                new MatteBorder(1, 0, 0, 0, Theme.BORDER),
                new EmptyBorder(26, 34, 32, 34)));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel title = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        title.setOpaque(false);
        title.setAlignmentX(LEFT_ALIGNMENT);
        weekday.setFont(EthiopicFonts.serif(Font.BOLD, 23f));
        weekday.setForeground(Theme.EMERALD);
        // Sans, not serif: this line mixes Ethiopic with ASCII ("ዓ.ም", and Western
        // digits when the Ge'ez toggle is off), and the serif face has no Latin.
        amDate.setFont(EthiopicFonts.sans(Font.PLAIN, 18f));
        amDate.setForeground(Theme.MUTED);
        title.add(weekday);
        title.add(amDate);
        add(title);

        gregFull.setFont(EthiopicFonts.sans(Font.PLAIN, 14.5f));
        gregFull.setForeground(Theme.GREG_MUTED);
        gregFull.setAlignmentX(LEFT_ALIGNMENT);
        gregFull.setBorder(new EmptyBorder(5, 1, 0, 0));
        add(gregFull);

        rows.setOpaque(false);
        rows.setLayout(new BoxLayout(rows, BoxLayout.Y_AXIS));
        rows.setAlignmentX(LEFT_ALIGNMENT);
        rows.setBorder(new EmptyBorder(18, 0, 0, 0));
        // Reserve a fixed height (room for the maximum of four event rows plus the
        // top inset) so the panel — and therefore the whole window — has a constant
        // height. Without this, a day with more events than the one the window was
        // packed for would push the layout past the frame and squish the grid.
        Dimension rowsSize = new Dimension(1, 18 + 4 * EVENT_ROW_HEIGHT);
        rows.setPreferredSize(rowsSize);
        rows.setMinimumSize(rowsSize);
        rows.setMaximumSize(new Dimension(Integer.MAX_VALUE, rowsSize.height));
        add(rows);
    }

    void update(CalendarModel model, DayCell day, boolean showGeez, boolean showGregorian) {
        int idx = (model.firstDayWeekIndex() + day.dayOfMonth() - 1) % 7;
        weekday.setText(model.weekdayNames()[idx]);
        // Amharic has no comma; the day and year sit side by side.
        String dayText = showGeez ? day.geez() : Integer.toString(day.dayOfMonth());
        String yearText = showGeez ? model.geezYearLabel() : model.amharicYearLabel();
        amDate.setText(model.amharicMonthName() + " " + dayText + " " + yearText);

        // Keep the line present (blank when hidden) so the panel height stays fixed.
        gregFull.setText(showGregorian ? day.gregFull() : " ");

        rows.removeAll();
        List<CalendarEvent> events = model.eventsFor(day);
        if (events == null || events.isEmpty()) {
            JLabel none = new JLabel("ምንም በዓል የለም");
            none.setFont(EthiopicFonts.sans(Font.PLAIN, 15f));
            none.setForeground(Theme.DETAIL_NONE);
            none.setBorder(new EmptyBorder(14, 0, 0, 0));
            none.setAlignmentX(LEFT_ALIGNMENT);
            rows.add(none);
        } else {
            for (CalendarEvent e : events) {
                rows.add(eventRow(e));
            }
        }
        rows.revalidate();
        rows.repaint();
    }

    private JPanel eventRow(CalendarEvent e) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, Theme.GRID_LINE),
                new EmptyBorder(12, 0, 12, 0)));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, EVENT_ROW_HEIGHT));

        JLabel cat = new JLabel(e.category());
        cat.setFont(EthiopicFonts.sans(Font.PLAIN, 14.5f));
        cat.setForeground(Theme.DETAIL_CAT);
        cat.setPreferredSize(new Dimension(240, 24));
        cat.setVerticalAlignment(SwingConstants.CENTER);

        JLabel val = new JLabel(e.name());
        val.setFont(EthiopicFonts.sans(Font.BOLD, 16f));
        val.setForeground(Theme.DETAIL_VAL);

        row.add(cat, BorderLayout.WEST);
        row.add(val, BorderLayout.CENTER);
        return row;
    }
}
