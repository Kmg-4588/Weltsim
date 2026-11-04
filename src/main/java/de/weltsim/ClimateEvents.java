package de.weltsim;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Locale;

@Mod.EventBusSubscriber(modid = WeltSimMod.MODID)
public class ClimateEvents {

    public static String status(ClimateData d) {
        return String.format(Locale.GERMANY,
                "CO2: %.0f ppm | Erwaermung: %+.1f °C | Meeresanstieg: %d Bloecke",
                d.getCo2(), d.getWarming(), d.getSeaRise());
    }

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent e) {
        if (e.phase != TickEvent.Phase.END || !(e.level instanceof ServerLevel level)
                || level.dimension() != Level.OVERWORLD) return;
        long t = level.getGameTime();
        ClimateData d = ClimateData.get(level.getServer());
        if (t % 200 == 0) cycle(level, d);
        if (t % 40 == 0 && Config.EFFECTS.get()) effects(level, d);
    }

    /** Natürliche Senken: Wälder und Ozeane nehmen überschüssiges CO2 auf. */
    private static void cycle(ServerLevel lvl, ClimateData d) {
        RandomSource r = lvl.random;
        double forest = 0.12;
        int n = 0, leaves = 0;
        for (ServerPlayer p : lvl.players()) {
            for (int i = 0; i < 40; i++) {
                int x = p.getBlockX() + r.nextInt(65) - 32;
                int z = p.getBlockZ() + r.nextInt(65) - 32;
                if (!lvl.hasChunkAt(new BlockPos(x, 64, z))) continue;
                int h = lvl.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z) - 1;
                n++;
                if (lvl.getBlockState(new BlockPos(x, h, z)).is(BlockTags.LEAVES)) leaves++;
            }
        }
        if (n > 0) forest = (double) leaves / n;
        double excess = d.getCo2() - ClimateData.BASE;
        d.setCo2(d.getCo2() - excess * 0.003 * (0.5 + 3 * forest));
        d.addCooling(-d.getCooling() * 0.01);
    }

    private static void effects(ServerLevel lvl, ClimateData d) {
        double w = d.getWarming();
        int rise = d.getSeaRise();
        if (Math.abs(w) < 1.0 && rise == 0) return;
        RandomSource r = lvl.random;
        int limit = lvl.getSeaLevel() - 1 + rise;

        for (ServerPlayer p : lvl.players()) {
            for (int i = 0; i < Config.SAMPLES.get(); i++) {
                int x = p.getBlockX() + r.nextInt(97) - 48;
                int z = p.getBlockZ() + r.nextInt(97) - 48;
                if (!lvl.hasChunkAt(new BlockPos(x, 64, z))) continue;
                int h = lvl.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);
                BlockPos top = new BlockPos(x, h, z);
                BlockPos below = top.below();
                BlockState ts = lvl.getBlockState(top);
                BlockState bs = lvl.getBlockState(below);

                // Schmelzen
                if (w > 1.0 && r.nextDouble() < Math.min(1.0, (w - 1.0) / 4.0)) {
                    if (ts.is(Blocks.SNOW)) { lvl.setBlock(top, Blocks.AIR.defaultBlockState(), 3); continue; }
                    if (bs.is(Blocks.SNOW_BLOCK) || bs.is(Blocks.POWDER_SNOW)) { lvl.setBlock(below, Blocks.AIR.defaultBlockState(), 3); continue; }
                    if (bs.is(Blocks.ICE)) { lvl.setBlock(below, Blocks.WATER.defaultBlockState(), 3); continue; }
                    if (bs.is(Blocks.PACKED_ICE)) { lvl.setBlock(below, Blocks.ICE.defaultBlockState(), 3); continue; }
                    if (bs.is(Blocks.BLUE_ICE)) { lvl.setBlock(below, Blocks.PACKED_ICE.defaultBlockState(), 3); continue; }
                }
                // Austrocknen
                if (w > 3.0 && r.nextDouble() < Math.min(1.0, (w - 3.0) / 6.0)) {
                    if (bs.is(Blocks.GRASS_BLOCK)) { lvl.setBlock(below, Blocks.COARSE_DIRT.defaultBlockState(), 3); continue; }
                    if (w > 5.0 && bs.is(Blocks.COARSE_DIRT)) { lvl.setBlock(below, Blocks.SAND.defaultBlockState(), 3); continue; }
                }
                // Vereisen (kalte Phase, z.B. nuklearer Winter)
                if (w < -1.0 && r.nextDouble() < Math.min(1.0, (-w - 1.0) / 4.0)) {
                    if (bs.is(Blocks.WATER) && lvl.getFluidState(below).isSource()) { lvl.setBlock(below, Blocks.ICE.defaultBlockState(), 3); continue; }
                    if (ts.isAir() && Blocks.SNOW.defaultBlockState().canSurvive(lvl, top)) { lvl.setBlock(top, Blocks.SNOW.defaultBlockState(), 3); continue; }
                }
                // Meeresanstieg
                if (rise > 0 && h <= limit && lvl.canSeeSky(top) && (ts.isAir() || ts.canBeReplaced())) {
                    FluidState fb = lvl.getFluidState(below);
                    boolean wet = fb.isSource() && fb.is(FluidTags.WATER);
                    if (!wet) {
                        for (Direction dir : Direction.Plane.HORIZONTAL) {
                            FluidState f = lvl.getFluidState(top.relative(dir));
                            if (f.isSource() && f.is(FluidTags.WATER)) { wet = true; break; }
                        }
                    }
                    if (wet) lvl.setBlock(top, Blocks.WATER.defaultBlockState(), 3);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onExplosion(ExplosionEvent.Detonate e) {
        if (e.getLevel() instanceof ServerLevel s) ClimateData.get(s.getServer()).addCo2(0.5);
    }

    @SubscribeEvent
    public static void commands(RegisterCommandsEvent e) {
        e.getDispatcher().register(Commands.literal("klima")
                .executes(c -> { show(c.getSource()); return 1; })
                .then(Commands.literal("setzen").requires(s -> s.hasPermission(2))
                        .then(Commands.argument("ppm", DoubleArgumentType.doubleArg(150, 4000))
                                .executes(c -> {
                                    ClimateData.get(c.getSource().getServer())
                                            .setCo2(DoubleArgumentType.getDouble(c, "ppm"));
                                    show(c.getSource());
                                    return 1;
                                }))));
    }

    private static void show(CommandSourceStack s) {
        String m = status(ClimateData.get(s.getServer()));
        s.sendSuccess(() -> Component.literal(m), false);
    }
}
