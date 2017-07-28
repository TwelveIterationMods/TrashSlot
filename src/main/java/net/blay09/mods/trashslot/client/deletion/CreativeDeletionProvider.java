package net.blay09.mods.trashslot.client.deletion;

import net.blay09.mods.trashslot.client.SlotTrash;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

public class CreativeDeletionProvider extends DefaultDeletionProvider {
	@Override
	public void undeleteLast(EntityPlayer player, SlotTrash trashSlot, boolean isRightClick) {
		// No going back in this mode.
	}

	@Override
	public boolean canUndeleteLast() {
		return false;
	}

	@Override
	public void deleteMouseItem(EntityPlayer player, ItemStack mouseItem, SlotTrash trashSlot, boolean isRightClick) {
		super.deleteMouseItem(player, mouseItem, trashSlot, isRightClick);
		emptyTrashSlot(trashSlot);
	}

	@Override
	public void deleteContainerItem(Container container, int slotNumber, boolean isDeleteAll) {
		// Do nothing. Instant deletion via DEL seems like a terrible idea.
	}
}
