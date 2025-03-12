package net.blay09.mods.trashslot;

import net.blay09.mods.balm.api.Balm;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class TrashHelper {

    private static final String KEY = "TrashSlot";

    public static void setTrashItem(Player player, ItemStack itemStack) {
        CompoundTag entityData = Balm.getHooks().getPersistentData(player);
        if (itemStack.isEmpty()) {
            entityData.remove(KEY);
        } else {
            entityData.put(KEY, itemStack.save(player.registryAccess()));
        }
    }

    public static ItemStack getTrashItem(Player player) {
        CompoundTag entityData = Balm.getHooks().getPersistentData(player);
        return entityData.getCompound(KEY)
                .flatMap(it -> ItemStack.parse(player.registryAccess(), it))
                .orElse(ItemStack.EMPTY);
    }

}
