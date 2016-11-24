package net.blay09.mods.trashslot.client.deletion;

import net.blay09.mods.trashslot.net.MessageDelete;
import net.blay09.mods.trashslot.net.MessageTrashSlotClick;
import net.blay09.mods.trashslot.net.NetworkHandler;
import net.blay09.mods.trashslot.client.SlotTrash;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

public class DefaultDeletionProvider implements DeletionProvider {
	@Override
	public void undeleteLast(EntityPlayer player, SlotTrash trashSlot) {
		player.inventory.setItemStack(trashSlot.getStack());
		trashSlot.putStack(ItemStack.EMPTY);
		NetworkHandler.instance.sendToServer(new MessageTrashSlotClick(ItemStack.EMPTY));
	}

	@Override
	public boolean canUndeleteLast() {
		return true;
	}

	@Override
	public void deleteMouseItem(EntityPlayer player, ItemStack mouseItem, SlotTrash trashSlot) {
		player.inventory.setItemStack(ItemStack.EMPTY);
		trashSlot.putStack(mouseItem);
		NetworkHandler.instance.sendToServer(new MessageTrashSlotClick(mouseItem));
	}

	@Override
	public void deleteContainerItem(Container container, int slotNumber, boolean isDeleteAll) {
		NetworkHandler.instance.sendToServer(new MessageDelete(slotNumber, isDeleteAll));
	}
}
