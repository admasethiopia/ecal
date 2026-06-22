package org.ecal;

final class CalendarMath {
    private CalendarMath() {
    }

    static long quotient(long dividend, long divisor) {
        return Math.floorDiv(dividend, divisor);
    }

    static int mod(long dividend, int divisor) {
        return (int) (dividend - divisor * quotient(dividend, divisor));
    }
}
