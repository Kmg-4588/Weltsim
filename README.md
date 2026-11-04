# Weltsimulator (Forge 1.20.1) – Phase 1: Klima

## Bauen
Voraussetzung: Java 17.
1. Forge-MDK für 1.20.1 (47.3.0) von files.minecraftforge.net laden und entpacken.
2. Aus diesem Projekt in den MDK-Ordner kopieren (vorhandene ersetzen): `src/`, `build.gradle`, `settings.gradle`, `gradle.properties`.
3. Im Ordner `gradlew build` ausführen (Windows: `gradlew.bat build`).
4. Fertige Mod: `build/libs/weltsim-0.1.0.jar` in den `mods`-Ordner legen.
Zum Testen aus der Entwicklungsumgebung: `gradlew runClient`.

## Inhalt
- Block "Fabrik": stößt CO2 aus (Craft: Eisenbarren, Ofen, Bruchstein).
- Item "Klimamesser": Rechtsklick zeigt CO2, Erwärmung, Meeresanstieg.
- Befehle: `/klima` und `/klima setzen <ppm>` (nur OP) zum Testen.
- Effekte bei Erwärmung: Schnee und Eis schmelzen, Gras trocknet aus, Meer steigt (max. 8 Blöcke).
- Bei starker Abkühlung (später: nuklearer Winter): Wasser friert, Schnee fällt.
- Einstellungen: `config/weltsim-common.toml`.
