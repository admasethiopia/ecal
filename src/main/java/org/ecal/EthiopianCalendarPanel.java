package org.ecal;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;

public final class EthiopianCalendarPanel extends JPanel {
    private static final DateTimeFormatter FULL_GREGORIAN_FORMAT = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
    private static final DateTimeFormatter SPAN_FORMAT = DateTimeFormatter.ofPattern("MMM d");
    private static final DateTimeFormatter SPAN_END_FORMAT = DateTimeFormatter.ofPattern("MMM d, yyyy");

    private final JComboBox<String> monthChooser = new JComboBox<>(EthiopianDate.MONTH_NAMES);
    private final JSpinner yearSpinner = new JSpinner(new SpinnerNumberModel(2016, 1, 9999, 1));
    private final JButton previousMonth = new JButton("‹ ቀዳሚ");
    private final JButton nextMonth = new JButton("ቀጣይ ›");
    private final JButton todayButton = new JButton("ዛሬ");
    private final JCheckBox ethiopicNumbers = new JCheckBox("የግዕዝ ቁጥር", true);
    private final JCheckBox gregorianDates = new JCheckBox("የፈረንጅ ቀን", true);
    private final JCheckBox showAllEvents = new JCheckBox("ሁሉም በዓላት", true);
    private final JSpinner dayFontSize = new JSpinner(new SpinnerNumberModel(22, 14, 34, 1));
    private final JLabel title = new JLabel();
    private final JLabel subtitle = new JLabel();
    private final JPanel weekdayHeader = new JPanel(new GridLayout(1, 7, 0, 0));
    private final JPanel dayGrid = new JPanel(new GridLayout(6, 7, 0, 0));
    private final JTextArea details = new JTextArea();
    private final CalendarDayButton[] dayButtons = new CalendarDayButton[42];
    private int visibleWeeks = -1;

    private EthiopianDate selectedDate = EthiopianDate.fromGregorian(LocalDate.now());
    private final EthiopianDate today = selectedDate;
    private boolean adjusting;

    public EthiopianCalendarPanel() {
        setLayout(new BorderLayout(0, 14));
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        add(buildHeader(), BorderLayout.NORTH);
        add(buildGrid(), BorderLayout.CENTER);
        add(buildDetails(), BorderLayout.SOUTH);
        wireEvents();
        installKeyBindings();
        showMonth(selectedDate.month(), selectedDate.year(), selectedDate);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(16, 10));
        header.setOpaque(false);

        title.setFont(Theme.ethiopicFont(26f, Font.PLAIN));
        title.setForeground(Theme.TEXT);
        subtitle.setFont(Theme.uiFont(12.5f, Font.PLAIN));
        subtitle.setForeground(Theme.MUTED_TEXT);
        JPanel titleStack = new JPanel();
        titleStack.setOpaque(false);
        titleStack.setLayout(new BoxLayout(titleStack, BoxLayout.Y_AXIS));
        title.setAlignmentX(LEFT_ALIGNMENT);
        subtitle.setAlignmentX(LEFT_ALIGNMENT);
        titleStack.add(title);
        titleStack.add(Box.createVerticalStrut(2));
        titleStack.add(subtitle);
        header.add(titleStack, BorderLayout.WEST);

        header.add(buildControls(), BorderLayout.EAST);
        return header;
    }

    private JPanel buildControls() {
        styleNavButton(previousMonth, "Previous month (Page Up)");
        styleNavButton(todayButton, "Go to today (Home)");
        styleNavButton(nextMonth, "Next month (Page Down)");

        JPanel navRow = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 8, 0));
        navRow.setOpaque(false);
        navRow.add(previousMonth);
        navRow.add(todayButton);
        navRow.add(nextMonth);

        monthChooser.setFont(Theme.ethiopicFont(13f, Font.PLAIN));
        monthChooser.setToolTipText("Month");
        yearSpinner.setFont(Theme.uiFont(12f, Font.PLAIN));
        yearSpinner.setEditor(new JSpinner.NumberEditor(yearSpinner, "0"));
        yearSpinner.setToolTipText("Year (Ethiopian / E.C.)");
        dayFontSize.setFont(Theme.uiFont(12f, Font.PLAIN));
        dayFontSize.setPreferredSize(new Dimension(58, 28));
        dayFontSize.setToolTipText("Day-number size");
        ethiopicNumbers.setOpaque(false);
        ethiopicNumbers.setFont(Theme.ethiopicFont(12.5f, Font.PLAIN));
        ethiopicNumbers.setToolTipText("Ge'ez numerals");
        gregorianDates.setOpaque(false);
        gregorianDates.setFont(Theme.ethiopicFont(12.5f, Font.PLAIN));
        gregorianDates.setToolTipText("Gregorian dates");
        showAllEvents.setOpaque(false);
        showAllEvents.setFont(Theme.ethiopicFont(12.5f, Font.PLAIN));
        showAllEvents.setToolTipText("Show all events");

        JPanel controlRow = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 8, 0));
        controlRow.setOpaque(false);
        controlRow.add(controlLabel("ወር", "Month"));
        controlRow.add(monthChooser);
        controlRow.add(controlLabel("ዓመት", "Year"));
        controlRow.add(yearSpinner);
        controlRow.add(controlLabel("መጠን", "Size"));
        controlRow.add(dayFontSize);
        controlRow.add(ethiopicNumbers);
        controlRow.add(gregorianDates);
        controlRow.add(showAllEvents);

        JPanel controls = new JPanel();
        controls.setOpaque(false);
        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));
        navRow.setAlignmentX(RIGHT_ALIGNMENT);
        controlRow.setAlignmentX(RIGHT_ALIGNMENT);
        controls.add(navRow);
        controls.add(Box.createVerticalStrut(8));
        controls.add(controlRow);
        return controls;
    }

    private JPanel buildGrid() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createLineBorder(Theme.BORDER));

        for (String dayName : EthiopianDate.WEEKDAY_NAMES) {
            JLabel label = new JLabel(dayName, JLabel.CENTER);
            label.setFont(Theme.ethiopicFont(12.5f, Font.PLAIN));
            label.setForeground(Theme.MUTED_TEXT);
            label.setOpaque(true);
            label.setBackground(Theme.ACCENT_SOFT);
            label.setBorder(Theme.CELL_BORDER);
            label.setPreferredSize(new Dimension(104, 34));
            weekdayHeader.add(label);
        }

        for (int i = 0; i < dayButtons.length; i++) {
            dayButtons[i] = new CalendarDayButton();
            int index = i;
            dayButtons[i].addActionListener(event -> selectFromButton(dayButtons[index]));
        }

        dayGrid.setOpaque(false);
        wrapper.add(weekdayHeader, BorderLayout.NORTH);
        wrapper.add(dayGrid, BorderLayout.CENTER);
        return wrapper;
    }

    private static JLabel controlLabel(String amharic, String englishTooltip) {
        JLabel label = new JLabel(amharic);
        label.setFont(Theme.ethiopicFont(12.5f, Font.BOLD));
        label.setForeground(Theme.TEXT);
        label.setToolTipText(englishTooltip);
        return label;
    }

    private static void styleNavButton(JButton button, String tooltip) {
        button.setFont(Theme.ethiopicFont(13f, Font.PLAIN));
        button.setToolTipText(tooltip);
        button.setFocusPainted(false);
    }

    private JScrollPane buildDetails() {
        details.setEditable(false);
        details.setRows(5);
        details.setLineWrap(true);
        details.setWrapStyleWord(true);
        details.setFont(Theme.ethiopicFont(15f, Font.PLAIN));
        details.setForeground(Theme.TEXT);
        details.setBackground(Theme.SURFACE);
        details.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        JScrollPane scrollPane = new JScrollPane(details);
        scrollPane.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        return scrollPane;
    }

    private void wireEvents() {
        previousMonth.addActionListener(event -> moveMonth(-1));
        nextMonth.addActionListener(event -> moveMonth(1));
        todayButton.addActionListener(event -> showMonth(today.month(), today.year(), today));
        monthChooser.addActionListener(event -> refreshFromControls());
        yearSpinner.addChangeListener(event -> refreshFromControls());
        ethiopicNumbers.addActionListener(event -> refresh());
        gregorianDates.addActionListener(event -> refresh());
        showAllEvents.addActionListener(event -> refresh());
        dayFontSize.addChangeListener(event -> refresh());
    }

    private void installKeyBindings() {
        InputMap gridMap = dayGrid.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap gridActions = dayGrid.getActionMap();
        bind(gridMap, gridActions, "LEFT", "prevDay", () -> moveSelection(-1));
        bind(gridMap, gridActions, "RIGHT", "nextDay", () -> moveSelection(1));
        bind(gridMap, gridActions, "UP", "prevWeek", () -> moveSelection(-7));
        bind(gridMap, gridActions, "DOWN", "nextWeek", () -> moveSelection(7));

        InputMap windowMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap windowActions = getActionMap();
        bind(windowMap, windowActions, "PAGE_UP", "prevMonth", () -> moveMonth(-1));
        bind(windowMap, windowActions, "PAGE_DOWN", "nextMonth", () -> moveMonth(1));
        bind(windowMap, windowActions, "HOME", "today", () -> showMonth(today.month(), today.year(), today));
    }

    private static void bind(InputMap inputMap, ActionMap actionMap, String key, String name, Runnable action) {
        inputMap.put(KeyStroke.getKeyStroke(key), name);
        actionMap.put(name, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                action.run();
            }
        });
    }

    private void selectFromButton(CalendarDayButton button) {
        if (button.date() != null) {
            selectedDate = button.date();
            refresh();
        }
    }

    private void refreshFromControls() {
        if (adjusting) {
            return;
        }
        int month = monthChooser.getSelectedIndex() + 1;
        long year = ((Number) yearSpinner.getValue()).longValue();
        int selectedDay = selectedDate != null && selectedDate.month() == month && selectedDate.year() == year ? selectedDate.day() : 1;
        int day = Math.min(selectedDay, EthiopianDate.lengthOfMonth(month, year));
        showMonth(month, year, new EthiopianDate(day, month, year));
    }

    private void moveMonth(int delta) {
        int month = monthChooser.getSelectedIndex() + 1 + delta;
        long year = ((Number) yearSpinner.getValue()).longValue();
        if (month < 1) {
            month = 13;
            year--;
        } else if (month > 13) {
            month = 1;
            year++;
        }
        if (year < 1 || year > 9999) {
            return;
        }
        int day = Math.min(selectedDate.day(), EthiopianDate.lengthOfMonth(month, year));
        showMonth(month, year, new EthiopianDate(day, month, year));
    }

    private void moveSelection(int deltaDays) {
        EthiopianDate target = EthiopianDate.fromFixedDay(selectedDate.toFixedDay() + deltaDays);
        if (target.year() < 1 || target.year() > 9999) {
            return;
        }
        showMonth(target.month(), target.year(), target);
    }

    private void showMonth(int month, long year, EthiopianDate dateToSelect) {
        selectedDate = dateToSelect;
        // Update the controls without letting their listeners re-enter and reset the selection.
        adjusting = true;
        monthChooser.setSelectedIndex(month - 1);
        yearSpinner.setValue((int) year);
        adjusting = false;
        refresh();
    }

    private void refresh() {
        int month = monthChooser.getSelectedIndex() + 1;
        long year = ((Number) yearSpinner.getValue()).longValue();
        updateTitle(month, year);

        int firstDayOfWeek = new EthiopianDate(1, month, year).dayOfWeek();
        int length = EthiopianDate.lengthOfMonth(month, year);
        int weeks = Math.max(1, Math.min(6, (firstDayOfWeek + length + 6) / 7));
        layoutWeeks(weeks);

        boolean ethiopic = ethiopicNumbers.isSelected();
        boolean gregorian = gregorianDates.isSelected();
        boolean allEvents = showAllEvents.isSelected();
        float fontSize = ((Number) dayFontSize.getValue()).floatValue();

        CalendarDayButton selectedButton = null;
        for (int i = 0; i < weeks * 7; i++) {
            int day = i - firstDayOfWeek + 1;
            EthiopianDate date = day >= 1 && day <= length ? new EthiopianDate(day, month, year) : null;
            boolean isSelected = date != null && date.equals(selectedDate);
            dayButtons[i].setDate(date, isSelected, date != null && date.equals(today), ethiopic, gregorian, allEvents, fontSize);
            if (isSelected) {
                selectedButton = dayButtons[i];
            }
        }

        updateDetails();
        keepDayFocus(selectedButton);
    }

    private void layoutWeeks(int weeks) {
        if (weeks == visibleWeeks) {
            return;
        }
        visibleWeeks = weeks;
        dayGrid.removeAll();
        dayGrid.setLayout(new GridLayout(weeks, 7, 0, 0));
        for (int i = 0; i < weeks * 7; i++) {
            dayGrid.add(dayButtons[i]);
        }
        dayGrid.revalidate();
        dayGrid.repaint();
    }

    /** Keep arrow-key navigation flowing by moving focus to the newly selected day. */
    private void keepDayFocus(CalendarDayButton selectedButton) {
        if (selectedButton == null) {
            return;
        }
        Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        if (focusOwner instanceof CalendarDayButton) {
            selectedButton.requestFocusInWindow();
        }
    }

    private void updateTitle(int month, long year) {
        title.setText(EthiopianDate.MONTH_NAMES[month - 1] + " " + EthiopicNumerals.format(year));
        LocalDate first = new EthiopianDate(1, month, year).toGregorian();
        LocalDate last = new EthiopianDate(EthiopianDate.lengthOfMonth(month, year), month, year).toGregorian();
        subtitle.setText(year + " ዓ.ም   ·   " + SPAN_FORMAT.format(first) + " – " + SPAN_END_FORMAT.format(last));
    }

    private void updateDetails() {
        if (selectedDate == null) {
            details.setText("");
            return;
        }

        LocalDate gregorian = selectedDate.toGregorian();
        StringBuilder text = new StringBuilder();
        text.append(selectedDate.monthName()).append(' ')
            .append(EthiopicNumerals.format(selectedDate.day()))
            .append(", ").append(EthiopicNumerals.format(selectedDate.year()))
            .append("    ")
            .append(FULL_GREGORIAN_FORMAT.format(gregorian))
            .append("\n\n");

        List<CalendarEvent> events = EventData.eventsFor(selectedDate);
        if (!showAllEvents.isSelected()) {
            events = events.stream().filter(CalendarEvent::routine).toList();
        }
        for (CalendarEvent event : events) {
            text.append(event.category()).append(": ").append(event.title()).append('\n');
        }
        details.setText(text.toString());
        details.setCaretPosition(0);
    }
}
