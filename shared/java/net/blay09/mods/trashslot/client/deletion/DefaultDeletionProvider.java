package net.blay09.mods.trashslot.client.deletion;

import net.blay09.mods.balm.network.BalmNetworking;
import net.blay09.mods.trashslot.network.DeleteFromSlotMessage;
import net.blay09.mods.trashslot.network.TrashSlotClickMessage;
import net.blay09.mods.trashslot.client.TrashSlotSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class DefaultDeletionProvider implements DeletionProvider {
	@Override
	public void undeleteLast(Player player, TrashSlotSlot slot, boolean isRightClick) {
		ItemStack trashStack = slot.getItem();
		ItemStack mouseStack = isRightClick ? trashStack.split(1) : trashStack;
		player.containerMenu.setCarried(mouseStack);
		slot.set(isRightClick ? trashStack : ItemStack.EMPTY);
		BalmNetworking.sendToServer(new TrashSlotClickMessage(ItemStack.EMPTY, isRightClick));
	}

	@Override
	public boolean canUndeleteLast() {
		return true;
	}

	@Override
	public void deleteMouseItem(Player player, ItemStack mouseItem, TrashSlotSlot slot, boolean isRightClick) {
		ItemStack mouseStack = mouseItem.copy();
		ItemStack trashStack = isRightClick ? mouseStack.split(1) : mouseStack;
		player.containerMenu.setCarried(isRightClick ? mouseStack : ItemStack.EMPTY);
		slot.set(trashStack);
		BalmNetworking.sendToServer(new TrashSlotClickMessage(mouseItem, isRightClick));
	}

	@Override
	public void deleteContainerItem(AbstractContainerMenu menu, int slotNumber, boolean isDeleteAll, TrashSlotSlot slot) {
		BalmNetworking.sendToServer(new DeleteFromSlotMessage(slotNumber, isDeleteAll));
	}

	@Override
	public void emptyTrashSlot(TrashSlotSlot slot) {
		slot.set(ItemStack.EMPTY);
		BalmNetworking.sendToServer(new DeleteFromSlotMessage(-1, false));
	}
}
