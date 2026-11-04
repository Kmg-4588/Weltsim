package de.weltsim;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, WeltSimMod.MODID);

    public static final RegistryObject<CreativeModeTab> MAIN = TABS.register("main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.weltsim"))
                    .icon(() -> new ItemStack(ModItems.FABRIK.get()))
                    .displayItems((params, out) -> {
                        out.accept(ModItems.FABRIK.get());
                        out.accept(ModItems.KLIMAMESSER.get());
                    }).build());
}
