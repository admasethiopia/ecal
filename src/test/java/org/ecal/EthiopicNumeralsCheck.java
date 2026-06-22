package org.ecal;

public final class EthiopicNumeralsCheck {
    private EthiopicNumeralsCheck() {
    }

    public static void main(String[] args) {
        run();
    }

    static void run() {
        assertFormat(0, "");
        assertFormat(1, "፩");
        assertFormat(9, "፱");
        assertFormat(10, "፲");
        assertFormat(11, "፲፩");
        assertFormat(18, "፲፰");
        assertFormat(20, "፳");
        assertFormat(99, "፺፱");
        // Regression: three-digit values used to drop their leading digit.
        assertFormat(100, "፻");
        assertFormat(101, "፻፩");
        assertFormat(110, "፻፲");
        assertFormat(207, "፪፻፯");
        assertFormat(999, "፱፻፺፱");
        // Regression: a trailing one before a place marker used to be dropped.
        assertFormat(1100, "፲፩፻");
        assertFormat(2018, "፳፻፲፰");
        assertFormat(9999, "፺፱፻፺፱");
        assertFormat(-5, "-፭");

        // Every value the app can display (years 1..9999, days 1..30) must be non-empty.
        for (long value = 1; value <= 9999; value++) {
            if (EthiopicNumerals.format(value).isEmpty()) {
                throw new AssertionError("Empty Ge'ez numeral for " + value);
            }
        }
        System.out.println("Ethiopic numeral checks passed.");
    }

    private static void assertFormat(long value, String expected) {
        String actual = EthiopicNumerals.format(value);
        if (!actual.equals(expected)) {
            throw new AssertionError("format(" + value + "): expected \"" + expected + "\" but got \"" + actual + "\"");
        }
    }
}
