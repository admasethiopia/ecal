# Ethiopica Calendrica

A small desktop Ethiopian calendar application.

This repository contains a cleaned Java/Swing implementation under `src/main/java/org/ecal`. It was migrated from a decompiled legacy jar; that jar is preserved at `reference/ec-original.jar` (gitignored) as the source of truth for the migration.

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

## Migration reference

The original application binary is `reference/ec-original.jar`. The earlier
`decompiled*/` directories were removed because they are regenerable from that
jar using the decompilers in `tools/`, e.g.:

```sh
java -jar tools/vineflower-1.11.1.jar reference/ec-original.jar decompiled-vineflower
```

`reference/`, `tools/`, and `build/` are gitignored.
