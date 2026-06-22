package org.ecal;

import java.util.Map;

public final class EthiopicNumerals {
    private static final Map<String, String> DIGITS = Map.ofEntries(
        Map.entry("1", "፩"),
        Map.entry("2", "፪"),
        Map.entry("3", "፫"),
        Map.entry("4", "፬"),
        Map.entry("5", "፭"),
        Map.entry("6", "፮"),
        Map.entry("7", "፯"),
        Map.entry("8", "፰"),
        Map.entry("9", "፱"),
        Map.entry("10", "፲"),
        Map.entry("20", "፳"),
        Map.entry("30", "፴"),
        Map.entry("40", "፵"),
        Map.entry("50", "፶"),
        Map.entry("60", "፷"),
        Map.entry("70", "፸"),
        Map.entry("80", "፹"),
        Map.entry("90", "፺"),
        Map.entry("100", "፻"),
        Map.entry("10000", "፼")
    );

    private EthiopicNumerals() {
    }

    public static String format(long value) {
        if (value == 0) {
            return "";
        }
        if (value < 0) {
            return "-" + format(-value);
        }
        if (value < 10) {
            return DIGITS.get(Long.toString(value));
        }

        String decimal = Long.toString(value);
        StringBuilder groups = new StringBuilder();
        int index = decimal.length() - 1;
        int groupIndex = 0;

        while (index > 0) {
            int ones = decimal.charAt(index--) - '0';
            int tens = decimal.charAt(index--) - '0';
            StringBuilder group = new StringBuilder();
            if (tens != 0) {
                group.append('`').append(tens).append('0');
            }
            if (ones > 1 || ones == 1 && groupIndex == 0) {
                group.append('`').append(ones);
            }
            if (groupIndex != 0) {
                if (groupIndex % 2 != 0) {
                    group.append("`100");
                }
                for (int i = 0; i < groupIndex / 2; i++) {
                    group.append("`10000");
                }
            }
            groups.insert(0, group).insert(0, ',');
            groupIndex++;
        }

        StringBuilder result = new StringBuilder();
        for (String group : groups.toString().split(",")) {
            if (group.isBlank()) {
                continue;
            }
            for (String token : group.split("`")) {
                if (!token.isBlank()) {
                    result.append(DIGITS.get(token));
                }
            }
        }
        return result.toString();
    }
}
