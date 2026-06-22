package org.ecal;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;

public final class EthiopianCalendarPanel extends JPanel {
    private static final DateTimeFormatter FULL_GREGORIAN_FORMAT = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");

    private final JComboBox<String> monthChooser = new JComboBox<>(EthiopianDate.MONTH_NAMES);
    private final JSpinner yearSpinner = new JSpinner(new SpinnerNumberModel(2016, 1, 9999, 1));
    private final JButton previousMonth = new JButton("Previous");
    private final JButton nextMonth = new JButton("Next");
    private final JButton todayButton = new JButton("Today");
    private final JCheckBox ethiopicNumbers = new JCheckBox("Ethiopic numerals", true);
    private final JCheckBox gregorianDates = new JCheckBox("Gregorian dates", true);
    private final JCheckBox showAllEvents = new JCheckBox("Show all events", true);
    private final JSpinner dayFontSize = new JSpinner(new SpinnerNumberModel(21, 14, 30, 1));
    private final JLabel title = new JLabel();
    private final JPanel weekdayHeader = new JPanel(new GridLayout(1, 7, 0, 0));
    private final JPanel dayGrid = new JPanel(new GridLayout(6, 7, 0, 0));
    private final JTextArea details = new JTextArea();
    private final CalendarDayButton[] dayButtons = new CalendarDayButton[42];

    private EthiopianDate selectedDate = EthiopianDate.fromGregorian(LocalDate.now());
    private EthiopianDate today = selectedDate;

    public EthiopianCalendarPanel() {
        setLayout(new BorderLayout(12, 12));
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        add(buildHeader(), BorderLayout.NORTH);
        add(buildGrid(), BorderLayout.CENTER);
        add(buildDetails(), BorderLayout.SOUTH);
        wireEvents();
        showMonth(selectedDate.month(), selectedDate.year(), selectedDate);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(12, 10));
        header.setOpaque(false);

        title.setFont(Theme.ethiopicFont(22f, Font.PLAIN));
        title.setForeground(Theme.TEXT);
        header.add(title, BorderLayout.WEST);

        styleActionButton(previousMonth, 82);
        styleActionButton(todayButton, 68);
        styleActionButton(nextMonth, 58);

        JToolBar navigation = new JToolBar();
        navigation.setFloatable(false);
        navigation.setOpaque(false);
        navigation.add(previousMonth);
        navigation.add(todayButton);
        navigation.add(nextMonth);
        header.add(navigation, BorderLayout.CENTER);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        controls.setOpaque(false);
        monthChooser.setFont(Theme.ethiopicFont(13f, Font.PLAIN));
        yearSpinner.setFont(Theme.uiFont(12f, Font.PLAIN));
        yearSpinner.setEditor(new JSpinner.NumberEditor(yearSpinner, "0"));
        ethiopicNumbers.setFont(Theme.uiFont(12f, Font.PLAIN));
        gregorianDates.setFont(Theme.uiFont(12f, Font.PLAIN));
        showAllEvents.setFont(Theme.uiFont(12f, Font.PLAIN));
        dayFontSize.setFont(Theme.uiFont(12f, Font.PLAIN));
        dayFontSize.setPreferredSize(new Dimension(54, 26));
        controls.add(controlLabel("Month"));
        controls.add(monthChooser);
        controls.add(controlLabel("Year"));
        controls.add(yearSpinner);
        controls.add(controlLabel("Day size"));
        controls.add(dayFontSize);
        controls.add(ethiopicNumbers);
        controls.add(gregorianDates);
        controls.add(showAllEvents);
        header.add(controls, BorderLayout.EAST);
        return header;
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
            label.setPreferredSize(new Dimension(104, 32));
            weekdayHeader.add(label);
        }

        for (int i = 0; i < dayButtons.length; i++) {
            CalendarDayButton button = new CalendarDayButton();
            dayButtons[i] = button;
            dayGrid.add(button);
        }

        wrapper.add(weekdayHeader, BorderLayout.NORTH);
        wrapper.add(dayGrid, BorderLayout.CENTER);
        return wrapper;
    }

    private static JLabel controlLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Theme.uiFont(12f, Font.BOLD));
        label.setForeground(Theme.TEXT);
        return label;
    }

    private static void styleActionButton(JButton button, int width) {
        button.setFont(Theme.uiFont(12f, Font.BOLD));
        button.setPreferredSize(new Dimension(width, 28));
        button.setMinimumSize(new Dimension(width, 28));
        button.setFocusPainted(false);
    }

    private JScrollPane buildDetails() {
        details.setEditable(false);
        details.setRows(6);
        details.setLineWrap(true);
        details.setWrapStyleWord(true);
        details.setFont(Theme.ethiopicFont(15f, Font.PLAIN));
        details.setForeground(Theme.TEXT);
        details.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
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

        for (CalendarDayButton button : dayButtons) {
            button.addActionListener(event -> {
                if (button.date() != null) {
                    selectedDate = button.date();
                    refresh();
                }
            });
        }
    }

    private void refreshFromControls() {
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
        int day = Math.min(selectedDate.day(), EthiopianDate.lengthOfMonth(month, year));
        showMonth(month, year, new EthiopianDate(day, month, year));
    }

    private void showMonth(int month, long year, EthiopianDate dateToSelect) {
        selectedDate = dateToSelect;
        monthChooser.setSelectedIndex(month - 1);
        yearSpinner.setValue((int) year);
        refresh();
    }

    private void refresh() {
        int month = monthChooser.getSelectedIndex() + 1;
        long year = ((Number) yearSpinner.getValue()).longValue();
        title.setText(EthiopianDate.MONTH_NAMES[month - 1] + " " + EthiopicNumerals.format(year));

        int firstDayOfWeek = new EthiopianDate(1, month, year).dayOfWeek();
        int length = EthiopianDate.lengthOfMonth(month, year);
        for (int i = 0; i < dayButtons.length; i++) {
            int day = i - firstDayOfWeek + 1;
            EthiopianDate date = day >= 1 && day <= length ? new EthiopianDate(day, month, year) : null;
            dayButtons[i].setDate(
                date,
                date != null && date.equals(selectedDate),
                date != null && date.equals(today),
                ethiopicNumbers.isSelected(),
                gregorianDates.isSelected(),
                showAllEvents.isSelected(),
                ((Number) dayFontSize.getValue()).floatValue()
            );
        }
        updateDetails();
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
        if (!showAllEvents.isSelected() && !events.isEmpty()) {
            events = List.of(events.get(0));
        }
        for (CalendarEvent event : events) {
            text.append(event.category()).append(": ").append(event.title()).append('\n');
        }
        details.setText(text.toString());
        details.setCaretPosition(0);
    }
}
