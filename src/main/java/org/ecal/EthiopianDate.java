package org.ecal;

import java.time.LocalDate;
import java.util.Objects;

public final class EthiopianDate {
    static final long EPOCH = 2796L;

    public static final String[] MONTH_NAMES = {
        "መስከረም", "ጥቅምት", "ኅዳር", "ታኅሣሥ", "ጥር", "የካቲት",
        "መጋቢት", "ሚያዝያ", "ግንቦት", "ሰኔ", "ሐምሌ", "ነሐሴ", "ጳጉሜን"
    };

    public static final String[] WEEKDAY_NAMES = {
        "እሑድ", "ሰኞ", "ማክሰኞ", "ረቡዕ", "ሐሙስ", "ዓርብ", "ቅዳሜ"
    };

    private final int day;
    private final int month;
    private final long year;

    public EthiopianDate(int day, int month, long year) {
        this.day = day;
        this.month = month;
        this.year = year;
        validate();
    }

    public static EthiopianDate fromFixedDay(long fixedDay) {
        long year = CalendarMath.quotient(4L * (fixedDay - EPOCH) + 1463L, 1461L);
        int month = 1 + (int) CalendarMath.quotient(fixedDay - toFixedDay(1, 1, year), 30L);
        int day = (int) (fixedDay - toFixedDay(1, month, year) + 1L);
        return new EthiopianDate(day, month, year);
    }

    public static EthiopianDate fromGregorian(LocalDate date) {
        return fromFixedDay(GregorianDate.toFixedDay(date.getDayOfMonth(), date.getMonthValue(), date.getYear()));
    }

    public static long toFixedDay(int day, int month, long year) {
        return EPOCH - 1L + 365L * (year - 1L) + CalendarMath.quotient(year, 4L) + 30L * (month - 1L) + day;
    }

    public long toFixedDay() {
        return toFixedDay(day, month, year);
    }

    public LocalDate toGregorian() {
        GregorianDate gregorian = GregorianDate.fromFixedDay(toFixedDay());
        return LocalDate.of((int) gregorian.year(), gregorian.month(), gregorian.day());
    }

    public boolean isLeapYear() {
        return isLeapYear(year);
    }

    public static boolean isLeapYear(long year) {
        return CalendarMath.mod(year, 4) == 3;
    }

    public static int lengthOfMonth(int month, long year) {
        if (month < 1 || month > 13) {
            throw new IllegalArgumentException("Month must be in the range 1..13");
        }
        if (month == 13) {
            return isLeapYear(year) ? 6 : 5;
        }
        return 30;
    }

    public int dayOfWeek() {
        int value = (int) (toFixedDay() % 7L);
        return value < 0 ? value + 7 : value;
    }

    public String monthName() {
        return MONTH_NAMES[month - 1];
    }

    public int day() {
        return day;
    }

    public int month() {
        return month;
    }

    public long year() {
        return year;
    }

    private void validate() {
        if (month < 1 || month > 13) {
            throw new IllegalArgumentException("Month must be in the range 1..13");
        }
        int maxDay = lengthOfMonth(month, year);
        if (day < 1 || day > maxDay) {
            throw new IllegalArgumentException("Day must be in the range 1.." + maxDay + " for this month");
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof EthiopianDate other)) {
            return false;
        }
        return day == other.day && month == other.month && year == other.year;
    }

    @Override
    public int hashCode() {
        return Objects.hash(day, month, year);
    }

    @Override
    public String toString() {
        return monthName() + " " + day + ", " + year;
    }
}
