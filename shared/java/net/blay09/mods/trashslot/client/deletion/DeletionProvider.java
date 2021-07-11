package net.blay09.mods.trashslot.client.deletion;

import net.blay09.mods.trashslot.client.TrashSlotSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public interface DeletionProvider {
	void undeleteLast(Player player, TrashSlotSlot slot, boolean isRightClick);
	boolean canUndeleteLast();
	void deleteMouseItem(Player player, ItemStack mouseItem, TrashSlotSlot slot, boolean isRightClick);
	void deleteContainerItem(AbstractContainerMenu menu, int slotNumber, boolean isDeleteAll, TrashSlotSlot slot);
	void emptyTrashSlot(TrashSlotSlot slot);
}
