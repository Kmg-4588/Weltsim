package de.weltsim;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;

/** Globaler Klimazustand der Welt (wird mit dem Spielstand gespeichert). */
public class ClimateData extends SavedData {
    public static final double BASE = 280.0;
    private double co2 = BASE;
    private double cooling = 0.0; // Abkuehlung durch Staub/nuklearen Winter (Grad)

    public static ClimateData get(MinecraftServer server) {
        return server.overworld().getDataStorage()
                .computeIfAbsent(ClimateData::load, ClimateData::new, "weltsim_climate");
    }

    public static ClimateData load(CompoundTag tag) {
        ClimateData d = new ClimateData();
        d.co2 = tag.contains("co2") ? tag.getDouble("co2") : BASE;
        d.cooling = tag.getDouble("cooling");
        return d;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putDouble("co2", co2);
        tag.putDouble("cooling", cooling);
        return tag;
    }

    public double getCo2() { return co2; }

    public void setCo2(double v) {
        co2 = Math.max(150.0, Math.min(4000.0, v));
        setDirty();
    }

    public void addCo2(double d) { setCo2(co2 + d); }

    public double getCooling() { return cooling; }

    public void addCooling(double c) {
        cooling = Math.max(0.0, Math.min(25.0, cooling + c));
        setDirty();
    }

    /** Erwärmung in Grad: ca. +3 Grad pro CO2-Verdopplung, minus Abkühlung. */
    public double getWarming() {
        return 4.33 * Math.log(co2 / BASE) - cooling;
    }

    public int getSeaRise() {
        double r = (getWarming() - 1.5) * 1.2;
        return (int) Math.max(0, Math.min(Config.MAX_SEA.get(), r));
    }
}
