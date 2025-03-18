package net.blay09.mods.trashslot.client.deletion;

import net.blay09.mods.trashslot.client.TrashSlotSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class CreativeDeletionProvider extends DefaultDeletionProvider {
    @Override
    public void undeleteLast(Player player, TrashSlotSlot trashSlot, boolean isRightClick) {
        // No going back in this mode.
    }

    @Override
    public boolean canUndeleteLast() {
        return false;
    }

    @Override
    public void deleteMouseItem(Player player, ItemStack mouseItem, TrashSlotSlot trashSlot, boolean isRightClick) {
        super.deleteMouseItem(player, mouseItem, trashSlot, isRightClick);
        emptyTrashSlot(trashSlot);
    }

    @Override
    public void deleteContainerItem(AbstractContainerMenu container, int slotNumber, boolean isDeleteAll, TrashSlotSlot trashSlot) {
        super.deleteContainerItem(container, slotNumber, isDeleteAll, trashSlot);
        emptyTrashSlot(trashSlot);
    }
}
