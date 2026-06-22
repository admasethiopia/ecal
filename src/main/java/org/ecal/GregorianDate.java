package org.ecal;

record GregorianDate(int day, int month, long year) {
    static long toFixedDay(int day, int month, long year) {
        int adjustment;
        if (month <= 2) {
            adjustment = 0;
        } else if (isLeapYear(year)) {
            adjustment = -1;
        } else {
            adjustment = -2;
        }

        return 365L * (year - 1L)
            + CalendarMath.quotient(year - 1L, 4L)
            - CalendarMath.quotient(year - 1L, 100L)
            + CalendarMath.quotient(year - 1L, 400L)
            + (367L * month - 362L) / 12L
            + adjustment
            + day;
    }

    static GregorianDate fromFixedDay(long fixedDay) {
        long year = yearFromFixedDay(fixedDay);
        int priorDays = (int) (fixedDay - toFixedDay(1, 1, year));
        int correction;
        if (fixedDay < toFixedDay(1, 3, year)) {
            correction = 0;
        } else if (isLeapYear(year)) {
            correction = 1;
        } else {
            correction = 2;
        }

        int month = (12 * (priorDays + correction) + 373) / 367;
        int day = (int) (fixedDay - toFixedDay(1, month, year) + 1L);
        return new GregorianDate(day, month, year);
    }

    static boolean isLeapYear(long year) {
        return CalendarMath.mod(year, 4) == 0
            && CalendarMath.mod(year, 400) != 100
            && CalendarMath.mod(year, 400) != 200
            && CalendarMath.mod(year, 400) != 300;
    }

    private static long yearFromFixedDay(long fixedDay) {
        long day = fixedDay - 1L;
        long n400 = CalendarMath.quotient(day, 146097L);
        int d400 = CalendarMath.mod(day, 146097);
        int n100 = (int) CalendarMath.quotient(d400, 36524L);
        int d100 = CalendarMath.mod(d400, 36524);
        int n4 = (int) CalendarMath.quotient(d100, 1461L);
        int d4 = CalendarMath.mod(d100, 1461);
        int n1 = (int) CalendarMath.quotient(d4, 365L);
        long year = 400L * n400 + 100L * n100 + 4L * n4 + n1;
        return n100 != 4 && n1 != 4 ? year + 1L : year;
    }
}
