package net.blay09.mods.trashslot.client.deletion;

import net.blay09.mods.trashslot.client.TrashSlotSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public interface DeletionProvider {
	void undeleteLast(Player player, TrashSlotSlot trashSlot, boolean isRightClick);
	boolean canUndeleteLast();
	void deleteMouseItem(Player player, ItemStack mouseItem, TrashSlotSlot trashSlot, boolean isRightClick);
	void deleteContainerItem(AbstractContainerMenu container, int slotNumber, boolean isDeleteAll, TrashSlotSlot slotTrash);
	void emptyTrashSlot(TrashSlotSlot trashSlot);
}
