package com.ethiopica.ui;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.KeyboardFocusManager;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * The full "Refined Classic" calendar card: header, controls, legend, weekday
 * row, month grid, and the selected-day detail panel.
 *
 * <p>All calendar data and navigation come from a {@link CalendarSource}; the
 * panel owns only UI state (selection and the display toggles).
 *
 * Usage:
 *   CalendarPanel panel = new CalendarPanel(mySource);
 *   frame.add(panel);
 */
public class CalendarPanel extends JPanel {

    private static final int MIN_YEAR = 1;
    private static final int MAX_YEAR = 9999;

    /** Source of all month data and calendar arithmetic (the backend adapter). */
    private final CalendarSource source;
    /** The currently displayed month. Swapped wholesale on every navigation. */
    private CalendarModel model;

    // ----- UI state (owned by the panel, never by the model) --------------
    private int selectedDay = -1;          // day-of-month currently selected
    private boolean showGregorian = true;  // show Gregorian dates (cells + detail)
    private boolean showFasting   = true;  // show the gold fasting tint
    private boolean showHoliday   = true;  // show the red feast dot
    private boolean showGeez      = true;  // Ge'ez numerals vs Western digits
    private float   numeralSize   = 30f;   // day-number font size

    /**
     * Guards the control listeners while we push state INTO the controls during
     * {@link #setMonthModel}, so syncing the month/year widgets doesn't recurse
     * back into a navigation.
     */
    private boolean adjusting;

    /** The non-blank day cells of the current month, for selection bookkeeping. */
    private final List<DayCellComponent> realCells = new ArrayList<>();

    // Header labels (month title + the muted year / Gregorian-range subtitle).
    private final JLabel monthLabel = new JLabel();
    private final JLabel amYearLabel = new JLabel();
    private final JLabel gregRangeLabel = new JLabel();

    // Controls row: month/year/size pickers and the three display toggles.
    private final JComboBox<String> monthChooser;
    private final JSpinner yearSpinner = new JSpinner(new SpinnerNumberModel(2016, MIN_YEAR, MAX_YEAR, 1));
    private final JSpinner sizeSpinner = new JSpinner(new SpinnerNumberModel(30, 16, 44, 1));
    private final JCheckBox geezToggle = new JCheckBox("ግዕዝ ቁጥር", true);
    private final JCheckBox gregToggle = new JCheckBox("ጎርጎርዮስ", true);
    private final JCheckBox allEventsToggle = new JCheckBox("ሁሉም በዓላት", true);

    private final JPanel weekdayPanel = new JPanel(new GridLayout(1, 7, 0, 0));
    private final JPanel gridPanel = new JPanel(new GridLayout(0, 7, 0, 0));
    private final DetailPanel detail = new DetailPanel();

    public CalendarPanel(CalendarSource source) {
        this.source = source;
        this.monthChooser = new JComboBox<>(source.monthNames());

        setBackground(Theme.CARD_BG);
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createLineBorder(Theme.BORDER, 1));

        // Build the unchanging frame (header/controls/legend/weekday/grid/detail),
        // wire the controls, and install keyboard navigation once...
        buildStaticChrome();
        wireControls();
        installKeyBindings();

        // ...then load today's month with today selected.
        selectedDay = source.todayDay();
        setMonthModel(source.forMonth(source.todayMonth(), source.todayYear()), selectedDay);
    }

    // ----- public API -----------------------------------------------------
    // Display toggles, also usable programmatically by an embedder. (The fasting
    // and holiday toggles have no on-screen control; they default on.)

    public void setShowGregorian(boolean v) { showGregorian = v; gregToggle.setSelected(v); rebuildGrid(); }
    public void setShowFasting(boolean v)   { showFasting = v;   rebuildGrid(); }
    public void setShowHoliday(boolean v)   { showHoliday = v;   rebuildGrid(); }

    // ----- model / month switching ----------------------------------------

    /**
     * Replace the displayed month with {@code m} and select {@code desiredDay}
     * (clamped to the month's length). This is the single choke point through
     * which every month change flows: it refreshes the header, re-syncs the
     * month/year controls (guarded so their listeners don't fire), rebuilds the
     * weekday row, and rebuilds the grid.
     */
    private void setMonthModel(CalendarModel m, int desiredDay) {
        this.model = m;
        this.selectedDay = clampDay(desiredDay, m);

        monthLabel.setText(m.amharicMonthName());
        amYearLabel.setText(m.amharicYearLabel());
        gregRangeLabel.setText("·   " + m.gregorianRangeLabel());

        // Mirror the model into the pickers without triggering onPickMonth().
        adjusting = true;
        monthChooser.setSelectedIndex(m.monthNumber() - 1);
        yearSpinner.setValue((int) m.yearNumber());
        adjusting = false;

        // Weekday names are model-supplied (Sunday-first) so the header always
        // matches the grid's column order.
        weekdayPanel.removeAll();
        for (String w : m.weekdayNames()) {
            JLabel l = new JLabel(w, SwingConstants.CENTER);
            l.setFont(EthiopicFonts.sans(Font.BOLD, 15.5f));
            l.setForeground(Theme.EMERALD);
            l.setBorder(new EmptyBorder(13, 0, 13, 0));
            weekdayPanel.add(l);
        }

        rebuildGrid();
        revalidate();
        repaint();
    }

    /** Build {@code (month, year)} via the source and select {@code desiredDay}. */
    private void goToMonth(int month, long year, int desiredDay) {
        setMonthModel(source.forMonth(month, year), desiredDay);
    }

    private static int clampDay(int day, CalendarModel m) {
        int n = m.days().size();
        if (day < 1) return 1;
        return Math.min(day, n);
    }

    // ----- chrome ---------------------------------------------------------

    private void buildStaticChrome() {
        addRow(buildHeader(), 0);
        addRow(buildControls(), 1);
        addRow(buildLegend(), 2);

        weekdayPanel.setBackground(Theme.WEEKDAY_BG);
        weekdayPanel.setBorder(new MatteBorder(1, 0, 1, 0, Theme.BORDER));
        addRow(weekdayPanel, 3);

        gridPanel.setBackground(Theme.CARD_BG);
        addRow(gridPanel, 4);

        addRow(detail, 5);
    }

    private void addRow(JComponent comp, int gridy) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = gridy;
        c.weightx = 1;
        c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTH;
        add(comp, c);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.CARD_BG);
        header.setBorder(new EmptyBorder(30, 34, 14, 34));

        // title block
        JPanel title = new JPanel();
        title.setOpaque(false);
        title.setLayout(new BoxLayout(title, BoxLayout.Y_AXIS));
        monthLabel.setFont(EthiopicFonts.serif(Font.BOLD, 40f));
        monthLabel.setForeground(Theme.EMERALD);
        monthLabel.setAlignmentX(LEFT_ALIGNMENT);
        title.add(monthLabel);

        JPanel sub = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        sub.setOpaque(false);
        sub.setAlignmentX(LEFT_ALIGNMENT);
        sub.setBorder(new EmptyBorder(10, 0, 0, 0));
        amYearLabel.setFont(EthiopicFonts.sans(Font.PLAIN, 15f));
        amYearLabel.setForeground(Theme.MUTED);
        gregRangeLabel.setFont(EthiopicFonts.sans(Font.PLAIN, 15f));
        gregRangeLabel.setForeground(Theme.MUTED);
        sub.add(amYearLabel);
        sub.add(gregRangeLabel);
        title.add(sub);

        header.add(title, BorderLayout.WEST);

        // navigation buttons
        JPanel nav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        nav.setOpaque(false);
        PillButton prev = new PillButton("‹ ቀዳሚ", false);
        PillButton today = new PillButton("ዛሬ", true);
        PillButton next = new PillButton("ቀጣይ ›", false);
        prev.addActionListener(e -> onPrev());
        today.addActionListener(e -> onToday());
        next.addActionListener(e -> onNext());
        nav.add(prev);
        nav.add(today);
        nav.add(next);
        header.add(nav, BorderLayout.EAST);

        return header;
    }

    private JPanel buildControls() {
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        controls.setBackground(Theme.CARD_BG);
        controls.setBorder(new EmptyBorder(0, 34, 16, 34));

        controls.add(fieldLabel("ወር"));
        monthChooser.setFont(EthiopicFonts.sans(Font.PLAIN, 15f));
        monthChooser.setToolTipText("Month");
        controls.add(monthChooser);

        controls.add(fieldLabel("ዓመት"));
        yearSpinner.setFont(EthiopicFonts.sans(Font.PLAIN, 15f));
        yearSpinner.setEditor(new JSpinner.NumberEditor(yearSpinner, "0"));
        yearSpinner.setPreferredSize(new Dimension(80, 32));
        yearSpinner.setToolTipText("Year (E.C.)");
        controls.add(yearSpinner);

        controls.add(fieldLabel("መጠን"));
        sizeSpinner.setFont(EthiopicFonts.sans(Font.PLAIN, 15f));
        sizeSpinner.setPreferredSize(new Dimension(70, 32));
        sizeSpinner.setToolTipText("Day-number size");
        controls.add(sizeSpinner);

        controls.add(Box.createHorizontalStrut(8));
        styleToggle(geezToggle, "Ge'ez numerals");
        styleToggle(gregToggle, "Gregorian dates");
        styleToggle(allEventsToggle, "Include everyday commemorations");
        controls.add(geezToggle);
        controls.add(gregToggle);
        controls.add(allEventsToggle);

        return controls;
    }

    private JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(EthiopicFonts.sans(Font.BOLD, 15f));
        l.setForeground(Theme.EMERALD);
        return l;
    }

    private void styleToggle(JCheckBox box, String tooltip) {
        box.setOpaque(false);
        box.setFont(EthiopicFonts.sans(Font.PLAIN, 14.5f));
        box.setForeground(Theme.LEGEND_TEXT);
        box.setToolTipText(tooltip);
        box.setFocusable(false);
    }

    private void wireControls() {
        monthChooser.addActionListener(e -> { if (!adjusting) onPickMonth(); });
        yearSpinner.addChangeListener(e -> { if (!adjusting) onPickMonth(); });
        sizeSpinner.addChangeListener(e -> {
            numeralSize = ((Number) sizeSpinner.getValue()).floatValue();
            rebuildGrid();
        });
        geezToggle.addActionListener(e -> { showGeez = geezToggle.isSelected(); rebuildGrid(); });
        gregToggle.addActionListener(e -> { showGregorian = gregToggle.isSelected(); rebuildGrid(); });
        allEventsToggle.addActionListener(e -> {
            source.setIncludeRoutine(allEventsToggle.isSelected());
            goToMonth(model.monthNumber(), model.yearNumber(), selectedDay);
        });
    }

    private void onPickMonth() {
        int month = monthChooser.getSelectedIndex() + 1;
        long year = ((Number) yearSpinner.getValue()).longValue();
        goToMonth(month, year, selectedDay);
    }

    private JPanel buildLegend() {
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 22, 0));
        legend.setBackground(Theme.CARD_BG);
        legend.setBorder(new EmptyBorder(0, 34, 20, 34));
        legend.add(legendItem("የጾም ቀናት", false));
        legend.add(legendItem("የበዓላት ቀን", true));
        return legend;
    }

    private JLabel legendItem(String text, boolean circle) {
        JLabel l = new JLabel(text, new SwatchIcon(circle), SwingConstants.LEFT);
        l.setFont(EthiopicFonts.sans(Font.PLAIN, 14f));
        l.setForeground(Theme.LEGEND_TEXT);
        l.setIconTextGap(8);
        return l;
    }

    // ----- grid -----------------------------------------------------------

    private void rebuildGrid() {
        if (model == null) return;
        gridPanel.removeAll();
        realCells.clear();

        int lead = model.firstDayWeekIndex();
        List<DayCell> days = model.days();
        // Always lay out a fixed 6-week grid (the maximum any month needs: a
        // 30-day month starting on Saturday spans 6 rows). A constant row count
        // keeps the packed window sized to fit every month — without it, a 6-row
        // month gets squished into a window packed for 5 rows.
        int cells = 6 * 7;

        // The first `lead` slots and any slots past the last day are inert blanks;
        // the rest carry a DayCell. Each cell reports clicks back via selectDay.
        for (int i = 0; i < cells; i++) {
            DayCell dc = (i >= lead && i < lead + days.size()) ? days.get(i - lead) : null;
            DayCellComponent comp = new DayCellComponent(
                    dc, showGregorian, showFasting, showHoliday, showGeez, numeralSize, this::selectDay);
            if (dc != null) realCells.add(comp);
            gridPanel.add(comp);
        }

        // Re-apply the selection to the freshly built cells (selectedDay was already
        // clamped to this month in setMonthModel).
        selectDay(dayByNumber(selectedDay));

        gridPanel.revalidate();
        gridPanel.repaint();
    }

    /** The DayCell for {@code dom}, or day 1 as a safe fallback. */
    private DayCell dayByNumber(int dom) {
        for (DayCell d : model.days()) {
            if (d.dayOfMonth() == dom) return d;
        }
        return model.days().get(0);
    }

    /** Mark {@code day} selected, update every cell's highlight, and refresh the detail panel. */
    private void selectDay(DayCell day) {
        selectedDay = day.dayOfMonth();
        DayCellComponent selectedComp = null;
        for (DayCellComponent c : realCells) {
            boolean on = c.day() != null && c.day().dayOfMonth() == selectedDay;
            c.setSelected(on);
            if (on) selectedComp = c;
        }
        detail.update(model, day, showGeez, showGregorian);
        keepDayFocus(selectedComp);
    }

    /** Keep arrow-key navigation flowing by following the selection with focus. */
    private void keepDayFocus(DayCellComponent selectedComp) {
        if (selectedComp == null) return;
        Component owner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        if (owner instanceof DayCellComponent) {
            selectedComp.requestFocusInWindow();
        }
    }

    // ----- navigation -----------------------------------------------------
    // Month steps keep the selected day-of-month (clamped); the source normalises
    // month 0/14 into the adjacent year, so we can pass month ± 1 directly.

    private void onPrev() { goToMonth(model.monthNumber() - 1, model.yearNumber(), selectedDay); }
    private void onNext() { goToMonth(model.monthNumber() + 1, model.yearNumber(), selectedDay); }
    private void onToday() { goToMonth(source.todayMonth(), source.todayYear(), source.todayDay()); }

    /** Arrow-key move: ask the source to resolve a relative day (crossing months), then go there. */
    private void moveSelection(int deltaDays) {
        long[] r = source.resolve(model.monthNumber(), model.yearNumber(), selectedDay, deltaDays);
        goToMonth((int) r[0], r[1], (int) r[2]);
    }

    private void installKeyBindings() {
        // Grid-level bindings (active while a day cell holds focus). Page/Home are
        // bound here as well as at window level so they win over the enclosing
        // JScrollPane's own page-scroll bindings during the ancestor phase.
        InputMap gridMap = gridPanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap gridActions = gridPanel.getActionMap();
        bind(gridMap, gridActions, "LEFT", "prevDay", () -> moveSelection(-1));
        bind(gridMap, gridActions, "RIGHT", "nextDay", () -> moveSelection(1));
        bind(gridMap, gridActions, "UP", "prevWeek", () -> moveSelection(-7));
        bind(gridMap, gridActions, "DOWN", "nextWeek", () -> moveSelection(7));
        bind(gridMap, gridActions, "PAGE_UP", "prevMonth", this::onPrev);
        bind(gridMap, gridActions, "PAGE_DOWN", "nextMonth", this::onNext);
        bind(gridMap, gridActions, "HOME", "today", this::onToday);

        // Window-level fallback so paging / today work even with no cell focused.
        InputMap windowMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap windowActions = getActionMap();
        bind(windowMap, windowActions, "PAGE_UP", "prevMonth", this::onPrev);
        bind(windowMap, windowActions, "PAGE_DOWN", "nextMonth", this::onNext);
        bind(windowMap, windowActions, "HOME", "today", this::onToday);
    }

    private static void bind(InputMap inputMap, ActionMap actionMap, String key, String name, Runnable action) {
        inputMap.put(KeyStroke.getKeyStroke(key), name);
        actionMap.put(name, new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { action.run(); }
        });
    }

    // ----- small painted swatch icon for the legend -----------------------

    private static final class SwatchIcon implements Icon {
        private final boolean circle;
        SwatchIcon(boolean circle) { this.circle = circle; }
        @Override public int getIconWidth()  { return circle ? 9 : 12; }
        @Override public int getIconHeight() { return circle ? 9 : 12; }
        @Override public void paintIcon(Component c, Graphics g0, int x, int y) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (circle) {
                g.setColor(Theme.HOLIDAY_DOT);
                g.fillOval(x, y + 1, 8, 8);
            } else {
                g.setColor(Theme.FASTING_BG);
                g.fillRoundRect(x, y, 11, 11, 4, 4);
                g.setColor(Theme.CHIP_SWATCH_BORDER);
                g.drawRoundRect(x, y, 11, 11, 4, 4);
            }
            g.dispose();
        }
    }
}
