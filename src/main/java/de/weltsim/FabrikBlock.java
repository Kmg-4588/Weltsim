package de.weltsim;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/** Stößt CO2 aus, solange der Chunk geladen ist. */
public class FabrikBlock extends Block {
    public FabrikBlock(Properties p) { super(p); }

    @Override
    public boolean isRandomlyTicking(BlockState state) { return true; }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
        ClimateData.get(level.getServer()).addCo2(Config.FABRIK_CO2.get());
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource rand) {
        if (rand.nextInt(5) == 0) {
            level.addParticle(ParticleTypes.LARGE_SMOKE,
                    pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5, 0.0, 0.05, 0.0);
        }
    }
}
