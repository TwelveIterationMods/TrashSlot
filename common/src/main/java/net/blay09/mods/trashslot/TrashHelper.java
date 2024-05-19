package net.blay09.mods.trashslot;

import net.blay09.mods.balm.api.Balm;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class TrashHelper {

    private static final String KEY = "TrashSlot";

    public static void setTrashItem(Player player, ItemStack itemStack) {
        CompoundTag entityData = Balm.getHooks().getPersistentData(player);
        entityData.put(KEY, itemStack.saveOptional(player.registryAccess()));
    }

    public static ItemStack getTrashItem(Player player) {
        CompoundTag entityData = Balm.getHooks().getPersistentData(player);
        CompoundTag trashSlot = entityData.getCompound(KEY);
        return ItemStack.parse(player.registryAccess(), trashSlot).orElse(ItemStack.EMPTY);
    }

}
