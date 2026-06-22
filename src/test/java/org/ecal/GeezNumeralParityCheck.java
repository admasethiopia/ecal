package org.ecal;

import com.ethiopica.ui.GeezNumerals;

/**
 * The UI carries its own Ge'ez numeral formatter ({@link GeezNumerals}, used for
 * day numbers in cells) while the domain has {@link EthiopicNumerals} (used for
 * the year label). They are independent implementations, so this check pins them
 * to identical output across the whole range the app shows — a divergence would
 * make a day and a year render in mismatched styles.
 */
public final class GeezNumeralParityCheck {

    private GeezNumeralParityCheck() {
    }

    public static void main(String[] args) {
        run();
    }

    static void run() {
        for (int n = 1; n <= 9999; n++) {
            String ui = GeezNumerals.toGeez(n);
            String domain = EthiopicNumerals.format(n);
            if (!ui.equals(domain)) {
                throw new AssertionError(
                    "Ge'ez numeral mismatch at " + n + ": UI=" + ui + " domain=" + domain);
            }
        }
        System.out.println("Ge'ez numeral parity checks passed.");
    }
}
