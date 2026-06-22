package com.ethiopica.ui;

/**
 * Converts Western integers to Ge'ez (Ethiopic) numerals.
 *
 * Correct for the ranges this UI needs — day-of-month (1–30) and years
 * (e.g. 2018 → ፳፻፲፰). Handles the hundreds (፻) and ten-thousands (፼)
 * separators and the rule that a leading unit "1" before a separator is
 * dropped (100 → ፻, not ፩፻). Numbers requiring stacked separators above
 * 10^6 are out of scope.
 */
public final class GeezNumerals {
    private GeezNumerals() {}

    private static final String[] ONES = {"", "፩", "፪", "፫", "፬", "፭", "፮", "፯", "፰", "፱"};
    private static final String[] TENS = {"", "፲", "፳", "፴", "፵", "፶", "፷", "፸", "፹", "፺"};

    public static String toGeez(int num) {
        if (num <= 0) return "";
        String s = Integer.toString(num);
        if (s.length() % 2 == 1) s = "0" + s;          // pad to whole 2-digit groups
        int groups = s.length() / 2;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < groups; i++) {
            int val = Integer.parseInt(s.substring(i * 2, i * 2 + 2));
            int power = groups - i - 1;                 // 0 = units, 1 = hundreds, 2 = ten-thousands
            if (val == 0) continue;                     // empty group contributes nothing

            String pair = TENS[val / 10] + ONES[val % 10];
            String marker = switch (power) {
                case 1 -> "፻";
                case 2 -> "፼";
                default -> "";
            };

            if (power >= 1 && val == 1) {
                sb.append(marker);                      // drop the leading unit "1" before a separator
            } else {
                sb.append(pair).append(marker);
            }
        }
        return sb.toString();
    }
}
