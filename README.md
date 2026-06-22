# Ethiopica Calendrica

A small desktop Ethiopian calendar application.

This repository now contains a cleaned Java/Swing implementation under `src/main/java/org/ecal`. The decompiled folders are kept only as migration reference material.

## Build

```sh
make
```

The runnable jar is written to:

```sh
build/ecal-clean.jar
```

## Run

```sh
make run
```

## Notes

- The calendar conversion logic is implemented in named domain classes: `EthiopianDate`, `GregorianDate`, and `CalendarMath`.
- The UI is implemented in `EthiopianCalendarPanel` and `CalendarDayButton`.
- The app bundles Noto Sans Ethiopic under the SIL Open Font License. See `third_party/notosansethiopic/OFL.txt`.
