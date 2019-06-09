package net.blay09.mods.trashslot.client.deletion;

import net.blay09.mods.trashslot.net.MessageDeleteFromSlot;
import net.blay09.mods.trashslot.net.MessageTrashSlotClick;
import net.blay09.mods.trashslot.net.NetworkHandler;
import net.blay09.mods.trashslot.client.SlotTrash;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;

public class DefaultDeletionProvider implements DeletionProvider {
	@Override
	public void undeleteLast(PlayerEntity player, SlotTrash trashSlot, boolean isRightClick) {
		ItemStack trashStack = trashSlot.getStack();
		ItemStack mouseStack = isRightClick ? trashStack.split(1) : trashStack;
		player.inventory.setItemStack(mouseStack);
		trashSlot.putStack(isRightClick ? trashStack : ItemStack.EMPTY);
		NetworkHandler.instance.sendToServer(new MessageTrashSlotClick(ItemStack.EMPTY, isRightClick));
	}

	@Override
	public boolean canUndeleteLast() {
		return true;
	}

	@Override
	public void deleteMouseItem(PlayerEntity player, ItemStack mouseItem, SlotTrash trashSlot, boolean isRightClick) {
		ItemStack mouseStack = mouseItem.copy();
		ItemStack trashStack = isRightClick ? mouseStack.split(1) : mouseStack;
		player.inventory.setItemStack(isRightClick ? mouseStack : ItemStack.EMPTY);
		trashSlot.putStack(trashStack);
		NetworkHandler.instance.sendToServer(new MessageTrashSlotClick(mouseItem, isRightClick));
	}

	@Override
	public void deleteContainerItem(Container container, int slotNumber, boolean isDeleteAll) {
		NetworkHandler.instance.sendToServer(new MessageDeleteFromSlot(slotNumber, isDeleteAll));
	}

	@Override
	public void emptyTrashSlot(SlotTrash trashSlot) {
		trashSlot.putStack(ItemStack.EMPTY);
		NetworkHandler.instance.sendToServer(new MessageDeleteFromSlot(-1, false));
	}
}
