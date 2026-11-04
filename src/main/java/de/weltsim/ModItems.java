package de.weltsim;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, WeltSimMod.MODID);

    public static final RegistryObject<Item> FABRIK = ITEMS.register("fabrik",
            () -> new BlockItem(ModBlocks.FABRIK.get(), new Item.Properties()));
    public static final RegistryObject<Item> KLIMAMESSER = ITEMS.register("klimamesser",
            () -> new KlimaMesserItem(new Item.Properties().stacksTo(1)));
}
