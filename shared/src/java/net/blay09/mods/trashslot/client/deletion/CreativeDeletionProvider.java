package net.blay09.mods.trashslot.client.deletion;

import net.blay09.mods.trashslot.client.SlotTrash;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class CreativeDeletionProvider extends DefaultDeletionProvider {
    @Override
    public void undeleteLast(Player player, SlotTrash trashSlot, boolean isRightClick) {
        // No going back in this mode.
    }

    @Override
    public boolean canUndeleteLast() {
        return false;
    }

    @Override
    public void deleteMouseItem(Player player, ItemStack mouseItem, SlotTrash trashSlot, boolean isRightClick) {
        super.deleteMouseItem(player, mouseItem, trashSlot, isRightClick);
        emptyTrashSlot(trashSlot);
    }

    @Override
    public void deleteContainerItem(AbstractContainerMenu container, int slotNumber, boolean isDeleteAll, SlotTrash trashSlot) {
        super.deleteContainerItem(container, slotNumber, isDeleteAll, trashSlot);
        emptyTrashSlot(trashSlot);
    }
}
