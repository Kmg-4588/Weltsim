package de.weltsim;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, WeltSimMod.MODID);

    public static final RegistryObject<Block> FABRIK = BLOCKS.register("fabrik",
            () -> new FabrikBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL).strength(4.0f, 6.0f)
                    .requiresCorrectToolForDrops().sound(SoundType.METAL)));
}
