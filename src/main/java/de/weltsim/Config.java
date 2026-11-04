package de.weltsim;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    private static final ForgeConfigSpec.Builder B = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.BooleanValue EFFECTS = B
            .comment("Klimaeffekte (Schmelzen, Austrocknen, Meeresanstieg, Vereisung) aktiv")
            .define("effekteAktiv", true);
    public static final ForgeConfigSpec.DoubleValue FABRIK_CO2 = B
            .comment("CO2-Ausstoss (ppm) pro Zufallstick einer Fabrik")
            .defineInRange("fabrikCo2", 0.3, 0.0, 100.0);
    public static final ForgeConfigSpec.IntValue MAX_SEA = B
            .comment("Maximaler Meeresanstieg in Bloecken")
            .defineInRange("maxMeeresanstieg", 8, 0, 30);
    public static final ForgeConfigSpec.IntValue SAMPLES = B
            .comment("Anzahl Stichproben pro Spieler und Durchlauf (mehr = schnellere Effekte, mehr Last)")
            .defineInRange("proben", 24, 1, 200);

    public static final ForgeConfigSpec SPEC = B.build();
}
