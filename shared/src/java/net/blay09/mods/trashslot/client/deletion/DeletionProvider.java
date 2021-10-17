package net.blay09.mods.trashslot.client.deletion;

import net.blay09.mods.trashslot.client.SlotTrash;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public interface DeletionProvider {
	void undeleteLast(Player player, SlotTrash trashSlot, boolean isRightClick);
	boolean canUndeleteLast();
	void deleteMouseItem(Player player, ItemStack mouseItem, SlotTrash trashSlot, boolean isRightClick);
	void deleteContainerItem(AbstractContainerMenu container, int slotNumber, boolean isDeleteAll, SlotTrash slotTrash);
	void emptyTrashSlot(SlotTrash trashSlot);
}
