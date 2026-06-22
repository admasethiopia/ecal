package org.ecal;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

final class EventData {
    private static final String[] MOVABLE_FASTS = {
        "ነነዌ", "0",
        "በአታ ጾመ (ሁዳዴ የሚገባበት)", "14",
        "ደብረ ዘይት", "41",
        "ሆሳእና", "62",
        "ስቅለት", "67",
        "ትንሳኤ", "69",
        "ረክበ ካህናት", "93",
        "እርገት", "108",
        "ጰራቅሊጦስ (ጰንጠቆስጤ)", "118",
        "ጾመ ሃዋሪያት (የሰኔ ጾም)", "119",
        "ጾመ ድህነት", "121"
    };

    static final String[] MONTHLY_COMMUNAL = {
        "ልደታ", "ታድዮስና፡አባጉባ", "በዓታ", "ዮሐንስ፡ሐዋርያው፡ወልደነጎድጓድ",
        "አቦ (አቡነ ገብረ ቅዱስ)", "ኢያሱስ", "ስላሴ", "አባ፡ኪሮስ", "ጨርቆስ",
        "መስቀል ኢየሱስ", "ሐና ማርያም", "ሚካኤል", "እግዚሐርአብ", "አቡነ፡አረጋዊ",
        "ቂርቆስ", "ኪዳነ፡ምህረት", "እስጢፋኖስ", "ቶማስ", "ገብርኤል",
        "ሕንፅተ፡ቤተ፡ለማርያም", "ማርያም", "ኡራኤል", "ጊዮርጊስ", "ተክለ ሐይማኖት",
        "መርቆሪዎስ", "ዮሴፍ", "መድሐኔ አለም", "አማኑኤል", "ባለ፡እግዚአብሔር",
        "ዮሐንስ፡እና፡ማርቆስ"
    };

    static final String[] PAGUME_COMMUNAL = {
        "አሮጊቷ፡ልደታ", "ታድዮስና፡አባጉባ", "ሩፋኤል", "ዮሐንስ፡ሐዋርያው፡ወልደነጎድጓድ",
        "አቦ (አቡነ ገብረ ቅዱስ)", "ኢያሱስ"
    };

    private static final Map<String, String> FASTS = mapOf(
        "21/3", "ካህና ሰማይ",
        "10/5", "ጾመ ገሃድ",
        "21/9", "ደብረ ምጥማቅ"
    );

    private static final Map<String, String> APOSTLES_AND_EVANGELISTS = mapOf(
        "1/1", "በርተሎሜዎስ",
        "12/2", "ማቴዎስ",
        "17/2", "እስጢፋኖስ",
        "22/2", "ሉቃስ",
        "18/3", "ፍሊጶስ",
        "4/4", "እንድርያስ",
        "4/5", "ዩሃንስ",
        "10/6", "ያቆብ ወልደ እልፍዩስ",
        "8/7", "ማትያስ",
        "17/8", "ያኮብ ወልደ ዘብዴዮስ",
        "30/8", "ማርቆስ",
        "26/9", "ቶማስ",
        "2/11", "ታድዮስ",
        "5/11", "ጴጥሮስ ወጳውሎስ (የጾም ሃዋሪያት ጾም መፍቻ)",
        "10/11", "ናትናኤል",
        "18/11", "ያዕቆብ የጌታ ወንድም"
    );

    private static final Map<String, String> FIXED_FEASTS = mapOf(
        "1/1", "ርእስ አውደ አመት (እንቁጣጣሽ)",
        "16/1", "ደመራ",
        "10/1", "መስቀል (የመጣበት)",
        "17/1", "መስቀል (የተገኘበት)",
        "15/3", "ጾመ ስብክት (የገና/የነቢያት ጾም)",
        "12/3", "ሚካአል",
        "19/4", "ገብርኤል",
        "19/6", "ገብርኤል",
        "29/4", "ልደት",
        "6/5", "ግርዘት",
        "7/5", "ስላሴ",
        "11/5", "ጥምቀት/ኤጲፋንያ",
        "12/5", "ቃና ዘገሊላ",
        "8/6", "ስምእን",
        "10/7", "በአለ መስቀል",
        "27/7", "መድሐኔ አለም",
        "29/7", "ትስብእት/በአለ ወልድ",
        "23/8", "ጊዮርጊስ",
        "13/12", "ደብረ ታቦር (ቡሄ)"
    );

    private static final Map<String, String> MARIAN_FEASTS = mapOf(
        "10/1", "ጼዴንያ",
        "6/3", "ደብረ ቁስቋም",
        "3/4", "በዓታ",
        "22/4", "ድቅሲዩስ",
        "28/4", "ገና",
        "29/4", "ልደት (ክርስቶስ የተወለደበት እለት)",
        "21/5", "እረፍት",
        "16/6", "ኪዳነ ምህረት",
        "29/7", "እንሰት",
        "1/9", "ልደት",
        "7/11", "ቁጽረታ",
        "1/12", "ጾም ፍልሰታ (የመቤታችን ጾም)",
        "16/12", "ፍልሰታ (ኪዳነ ምህረት) - የጾም መፍቻ",
        "3/13", "ሩፋኤል"
    );

    private static final Map<String, String> ARCHANGELS = mapOf(
        "1/1", "ራጉኤል",
        "8/3", "አፍኒን",
        "12/3", "ሚካኤል",
        "3/4", "ፋኑኤል",
        "27/5", "ሱርያል",
        "5/11", "ሳቁኤል",
        "21/11", "ኡራኤል",
        "3/13", "ሩፋኤል"
    );

    private EventData() {
    }

    static List<CalendarEvent> eventsFor(EthiopianDate date) {
        List<CalendarEvent> events = new ArrayList<>();
        int day = date.day();
        int month = date.month();
        String monthDay = day + "/" + month;

        if (month == 13) {
            addIfPresent(events, "ወርኃዊ በዓል", day <= PAGUME_COMMUNAL.length ? PAGUME_COMMUNAL[day - 1] : null);
        } else {
            addIfPresent(events, "ወርኃዊ በዓል", MONTHLY_COMMUNAL[day - 1]);
        }

        addIfPresent(events, "ተንቀሳቃሽ በዓል/ጾም", movableEvents(date.year()).get(month + "/" + day + "/" + date.year()));
        addIfPresent(events, "ጾም", FASTS.get(monthDay));
        addIfPresent(events, "አመታዊ በዓል", FIXED_FEASTS.get(monthDay));
        addIfPresent(events, "የኃዋሪያ/ወንጌላዊ በዓል", APOSTLES_AND_EVANGELISTS.get(monthDay));
        addIfPresent(events, "የቅድስት ማርያም በዓል", MARIAN_FEASTS.get(monthDay));
        addIfPresent(events, "የሊቃነ መላእክ እለት", ARCHANGELS.get(monthDay));
        return events;
    }

    static boolean hasEvents(EthiopianDate date) {
        return eventsFor(date).size() > 1;
    }

    private static Map<String, String> movableEvents(long ethiopianYear) {
        long ameteAlem = 5500L + ethiopianYear;
        long metonicRemainder = (ameteAlem - 1L) % 19L;
        long epact = metonicRemainder * 11L % 30L;
        long metqe = 30L - epact;
        int monthOffset = metqe < 15L ? 2 : 1;
        int leapDay = ethiopianYear % 4L == 0L ? 1 : 0;
        int quarterDays = (int) ((ameteAlem - 1L) / 4L);
        long yearStart = (ameteAlem - 1L) * 365L + quarterDays + leapDay;
        long metqeDay = yearStart + (monthOffset - 1L) * 30L + metqe;
        long weekday = metqeDay % 7L + 1L;
        long ninevehOffset = 129L - ((weekday + 1L) % 7L + 1L);

        Map<String, String> events = new LinkedHashMap<>();
        for (int i = 1; i < MOVABLE_FASTS.length; i += 2) {
            long absoluteDay = metqeDay + ninevehOffset + Integer.parseInt(MOVABLE_FASTS[i]);
            long month = (absoluteDay - yearStart) / 30L;
            long day = (absoluteDay - yearStart) % 30L;
            if (day > 0L) {
                month++;
            }
            if (day == 0L) {
                day = 30L;
            }
            events.put(month + "/" + day + "/" + ethiopianYear, MOVABLE_FASTS[i - 1]);
        }
        return events;
    }

    private static void addIfPresent(List<CalendarEvent> events, String category, String value) {
        if (value != null && !value.isBlank()) {
            events.add(new CalendarEvent(category, value));
        }
    }

    private static Map<String, String> mapOf(String... values) {
        Map<String, String> map = new LinkedHashMap<>();
        for (int i = 0; i < values.length; i += 2) {
            map.put(values[i], values[i + 1]);
        }
        return Map.copyOf(map);
    }
}
