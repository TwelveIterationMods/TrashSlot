package net.blay09.mods.trashslot.client.deletion;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.trashslot.network.MessageDeleteFromSlot;
import net.blay09.mods.trashslot.network.MessageTrashSlotClick;
import net.blay09.mods.trashslot.client.TrashSlotSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class DefaultDeletionProvider implements DeletionProvider {
	@Override
	public void undeleteLast(Player player, TrashSlotSlot trashSlot, boolean isRightClick) {
		ItemStack trashStack = trashSlot.getItem();
		ItemStack mouseStack = isRightClick ? trashStack.split(1) : trashStack;
		player.containerMenu.setCarried(mouseStack);
		trashSlot.set(isRightClick ? trashStack : ItemStack.EMPTY);
		Balm.getNetworking().sendToServer(new MessageTrashSlotClick(ItemStack.EMPTY, isRightClick));
	}

	@Override
	public boolean canUndeleteLast() {
		return true;
	}

	@Override
	public void deleteMouseItem(Player player, ItemStack mouseItem, TrashSlotSlot trashSlot, boolean isRightClick) {
		ItemStack mouseStack = mouseItem.copy();
		ItemStack trashStack = isRightClick ? mouseStack.split(1) : mouseStack;
		player.containerMenu.setCarried(isRightClick ? mouseStack : ItemStack.EMPTY);
		trashSlot.set(trashStack);
		Balm.getNetworking().sendToServer(new MessageTrashSlotClick(mouseItem, isRightClick));
	}

	@Override
	public void deleteContainerItem(AbstractContainerMenu container, int slotNumber, boolean isDeleteAll, TrashSlotSlot slotTrash) {
		Balm.getNetworking().sendToServer(new MessageDeleteFromSlot(slotNumber, isDeleteAll));
	}

	@Override
	public void emptyTrashSlot(TrashSlotSlot trashSlot) {
		trashSlot.set(ItemStack.EMPTY);
		Balm.getNetworking().sendToServer(new MessageDeleteFromSlot(-1, false));
	}
}
