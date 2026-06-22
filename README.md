# Ethiopica Calendrica

A small desktop **Ethiopian calendar** application (Java/Swing). It renders one
Ethiopian month at a time with the Gregorian equivalents, Ge'ez numerals, and the
feasts and fasts of the Ethiopian Orthodox calendar — including the movable feasts
computed from *Bahire Hasab* (the traditional Computus).

The UI is the "Refined Classic" design: a warm cream card with an emerald accent,
serif Ge'ez numerals, a gold tint on fasting days, and a red dot on feast days.

## Build

```sh
make
```

The runnable jar is written to `build/ecal-clean.jar`. It is a single,
self-contained jar with **no third-party runtime dependencies** — pure Swing,
custom-painted, with the Noto Ethiopic fonts bundled inside. Java **17+** is
required.

## Run

```sh
make run
```

The window sizes itself to the calendar and opens fully visible (no scrollbars).

### Controls

The controls row is bilingual — Amharic labels with English tooltips:

- **ወር / ዓመት** — pick the month and year (Ethiopian / E.C.).
- **መጠን** — day-number font size.
- **ግዕዝ ቁጥር** — toggle Ge'ez numerals vs. Western digits.
- **ጎርጎርዮስ** — toggle the Gregorian dates.
- **ሁሉም በዓላት** — include the everyday monthly commemoration in the day detail.
- **‹ ቀዳሚ / ዛሬ / ቀጣይ ›** — previous month / today / next month.

### Keyboard shortcuts

- **Arrow keys** — move the selected day (click a day first to give the grid focus).
- **Page Up / Page Down** — previous / next month.
- **Home** — jump to today.

## Architecture

The code is split into a calendar **domain** and a backend-agnostic **UI**, joined
by a thin **adapter**:

```
org.ecal/                         the calendar domain (no UI)
  EthiopianDate, GregorianDate    date types + conversion
  CalendarMath                    integer division/modulo helpers
  EthiopicNumerals                int → Ge'ez numerals
  CalendarEvent, EventData        feasts/fasts incl. the Bahire Hasab movable feasts
  EcalCalendarSource              ADAPTER: domain → UI seam  (the only bridge)
  EthiopicaCalendricaApp          main(): builds the window

com.ethiopica.ui/                 reusable Swing widget, backend-agnostic
  CalendarModel, CalendarSource   the seam an adapter implements
  CalendarPanel                   the whole calendar card (header/grid/detail)
  DayCellComponent, DetailPanel   custom-painted cell, selected-day detail
  PillButton                      rounded nav button
  DayCell, CalendarEvent          value types passed across the seam
  GeezNumerals, EthiopicFonts     UI numerals + bundled-font loader
  Theme                           colour palette
```

The UI never does calendar arithmetic. `CalendarPanel` asks a `CalendarSource`
to build a month (`forMonth`) or resolve a relative day move (`resolve`), and
renders the resulting `CalendarModel`. To put this widget on a different backend,
implement those two interfaces — `EcalCalendarSource` is the reference adapter
over this project's domain.

## Fonts

Two TrueType files are bundled on the classpath under `/fonts/` and loaded via
`Font.createFont`:

- `NotoSerifEthiopic-Regular.ttf` — serif Ge'ez numerals and the month title.
- `NotoSansEthiopic-Regular.ttf` — UI labels, Amharic body text, **and Latin**
  (Gregorian dates / Western digits). The serif face has no Latin glyphs, so all
  Latin/ASCII is drawn in the sans face to avoid missing-glyph boxes.

Both are Noto fonts under the SIL Open Font License (see
`third_party/notosansethiopic/OFL.txt`). If a font is missing the app still runs,
falling back to a logical font.

## Tests

```sh
make test
```

Hand-rolled checks (no test framework), run from `org.ecal.TestSuite`:

- `CalendarConversionCheck` — Ethiopian ⇄ Gregorian conversion.
- `EthiopicNumeralsCheck` — Ge'ez numeral formatting.
- `EventDataCheck` — movable feasts cross-checked against an independent Orthodox
  Computus, plus routine/notable event classification.
- `EcalCalendarSourceCheck` — the adapter: month normalisation, year clamping,
  day resolution across boundaries, and the feast/fast classification.
- `GeezNumeralParityCheck` — the UI and domain numeral formatters agree.
