package de.weltsim;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class KlimaMesserItem extends Item {
    public KlimaMesserItem(Properties p) { super(p); }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && level.getServer() != null) {
            player.sendSystemMessage(Component.literal(
                    ClimateEvents.status(ClimateData.get(level.getServer()))));
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
