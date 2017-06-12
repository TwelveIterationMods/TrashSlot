package net.blay09.mods.trashslot.client.deletion;

import net.blay09.mods.trashslot.client.SlotTrash;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

public interface DeletionProvider {
	void undeleteLast(EntityPlayer player, SlotTrash trashSlot, boolean isRightClick);
	boolean canUndeleteLast();
	void deleteMouseItem(EntityPlayer player, ItemStack mouseItem, SlotTrash trashSlot, boolean isRightClick);
	void deleteContainerItem(Container container, int slotNumber, boolean isDeleteAll);
	void emptyTrashSlot(SlotTrash trashSlot);
}
