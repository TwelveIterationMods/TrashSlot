package net.blay09.mods.trashslot.client.deletion;

import net.blay09.mods.trashslot.client.SlotTrash;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;

public interface DeletionProvider {
	void undeleteLast(PlayerEntity player, SlotTrash trashSlot, boolean isRightClick);
	boolean canUndeleteLast();
	void deleteMouseItem(PlayerEntity player, ItemStack mouseItem, SlotTrash trashSlot, boolean isRightClick);
	void deleteContainerItem(Container container, int slotNumber, boolean isDeleteAll);
	void emptyTrashSlot(SlotTrash trashSlot);
}
