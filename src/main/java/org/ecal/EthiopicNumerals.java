package org.ecal;

/**
 * Formats non-negative integers as Ge'ez (Ethiopic) numerals.
 *
 * <p>Ge'ez numerals are written in base-100 groups. Each two-digit group (1..99)
 * is rendered from a tens glyph and a ones glyph, and the groups are separated by
 * the place markers {@code ፻} (100) and {@code ፼} (10000). A group whose value is
 * exactly one is written as the bare place marker (e.g. 100 is {@code ፻}, not
 * {@code ፩፻}).
 */
public final class EthiopicNumerals {
    private static final String[] ONES = {"", "፩", "፪", "፫", "፬", "፭", "፮", "፯", "፰", "፱"};
    private static final String[] TENS = {"", "፲", "፳", "፴", "፵", "፶", "፷", "፸", "፹", "፺"};
    private static final String HUNDRED = "፻";
    private static final String TEN_THOUSAND = "፼";

    private EthiopicNumerals() {
    }

    public static String format(long value) {
        if (value == 0) {
            // Ge'ez has no zero glyph; callers treat an empty string as "nothing to show".
            return "";
        }
        if (value < 0) {
            return "-" + format(-value);
        }

        StringBuilder result = new StringBuilder();
        int position = 0;
        for (long remaining = value; remaining > 0; remaining /= 100) {
            int group = (int) (remaining % 100);
            if (group != 0) {
                result.insert(0, groupDigits(group, position) + placeMarker(position));
            }
            position++;
        }
        return result.toString();
    }

    private static String groupDigits(int group, int position) {
        if (group == 1 && position > 0) {
            // The leading "one" before a place marker is implied (100 -> ፻, not ፩፻).
            return "";
        }
        return TENS[group / 10] + ONES[group % 10];
    }

    private static String placeMarker(int position) {
        if (position == 0) {
            return "";
        }
        StringBuilder marker = new StringBuilder();
        if (position % 2 == 1) {
            marker.append(HUNDRED);
        }
        marker.append(TEN_THOUSAND.repeat(position / 2));
        return marker.toString();
    }
}
