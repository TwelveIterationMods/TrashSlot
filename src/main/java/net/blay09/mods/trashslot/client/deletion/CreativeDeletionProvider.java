package net.blay09.mods.trashslot.client.deletion;

import net.blay09.mods.trashslot.client.SlotTrash;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;

public class CreativeDeletionProvider extends DefaultDeletionProvider {
	@Override
	public void undeleteLast(PlayerEntity player, SlotTrash trashSlot, boolean isRightClick) {
		// No going back in this mode.
	}

	@Override
	public boolean canUndeleteLast() {
		return false;
	}

	@Override
	public void deleteMouseItem(PlayerEntity player, ItemStack mouseItem, SlotTrash trashSlot, boolean isRightClick) {
		super.deleteMouseItem(player, mouseItem, trashSlot, isRightClick);
		emptyTrashSlot(trashSlot);
	}

	@Override
	public void deleteContainerItem(Container container, int slotNumber, boolean isDeleteAll, SlotTrash trashSlot) {
		super.deleteContainerItem(container, slotNumber, isDeleteAll, trashSlot);
		emptyTrashSlot(trashSlot);
	}
}
