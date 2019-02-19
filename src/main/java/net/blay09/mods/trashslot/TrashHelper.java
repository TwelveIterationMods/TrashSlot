package net.blay09.mods.trashslot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TrashHelper {

	private static final String KEY = "TrashSlot";

	public static void setTrashItem(EntityPlayer player, ItemStack itemStack) {
		NBTTagCompound entityData = player.getEntityData();
		NBTTagCompound trashSlot = new NBTTagCompound();
		itemStack.write(trashSlot);
		entityData.setTag(KEY, trashSlot);
	}

	public static ItemStack getTrashItem(EntityPlayer player) {
		NBTTagCompound entityData = player.getEntityData();
		NBTTagCompound trashSlot = entityData.getCompound(KEY);
		return ItemStack.read(trashSlot);
	}

}
