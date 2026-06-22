package org.ecal;

public final class TestSuite {
    private TestSuite() {
    }

    public static void main(String[] args) {
        CalendarConversionCheck.run();
        EthiopicNumeralsCheck.run();
        EventDataCheck.run();
        EcalCalendarSourceCheck.run();
        GeezNumeralParityCheck.run();
        System.out.println("All checks passed.");
    }
}
