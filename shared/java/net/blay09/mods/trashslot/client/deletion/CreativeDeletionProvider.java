package net.blay09.mods.trashslot.client.deletion;

import net.blay09.mods.trashslot.client.TrashSlotSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class CreativeDeletionProvider extends DefaultDeletionProvider {
    @Override
    public void undeleteLast(Player player, TrashSlotSlot slot, boolean isRightClick) {
        // No going back in this mode.
    }

    @Override
    public boolean canUndeleteLast() {
        return false;
    }

    @Override
    public void deleteMouseItem(Player player, ItemStack mouseItem, TrashSlotSlot slot, boolean isRightClick) {
        super.deleteMouseItem(player, mouseItem, slot, isRightClick);
        emptyTrashSlot(slot);
    }

    @Override
    public void deleteContainerItem(AbstractContainerMenu menu, int slotNumber, boolean isDeleteAll, TrashSlotSlot slot) {
        super.deleteContainerItem(menu, slotNumber, isDeleteAll, slot);
        emptyTrashSlot(slot);
    }
}
