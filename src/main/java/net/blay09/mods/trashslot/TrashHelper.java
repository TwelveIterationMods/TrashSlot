package net.blay09.mods.trashslot;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class TrashHelper {

    private static final String KEY = "TrashSlot";

    public static void setTrashItem(PlayerEntity player, ItemStack itemStack) {
        CompoundNBT entityData = player.getEntityData();
        CompoundNBT trashSlot = new CompoundNBT();
        itemStack.write(trashSlot);
        entityData.put(KEY, trashSlot);
    }

    public static ItemStack getTrashItem(PlayerEntity player) {
        CompoundNBT entityData = player.getEntityData();
        CompoundNBT trashSlot = entityData.getCompound(KEY);
        return ItemStack.read(trashSlot);
    }

}
