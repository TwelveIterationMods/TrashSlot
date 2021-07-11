package net.blay09.mods.trashslot;

import net.blay09.mods.balm.entity.BalmPlayers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class TrashHelper {

    private static final String KEY = "TrashSlot";

    public static void setTrashItem(Player player, ItemStack itemStack) {
        CompoundTag entityData = BalmPlayers.getPersistentData(player);
        CompoundTag trashSlot = new CompoundTag();
        itemStack.save(trashSlot);
        entityData.put(KEY, trashSlot);
    }

    public static ItemStack getTrashItem(Player player) {
        CompoundTag entityData = BalmPlayers.getPersistentData(player);
        CompoundTag trashSlot = entityData.getCompound(KEY);
        return ItemStack.of(trashSlot);
    }

}
