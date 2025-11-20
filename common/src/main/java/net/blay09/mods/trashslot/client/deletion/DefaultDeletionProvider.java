package net.blay09.mods.trashslot.client.deletion;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.trashslot.api.ItemTrashedEvent;
import net.blay09.mods.trashslot.api.ItemUntrashedEvent;
import net.blay09.mods.trashslot.api.TrashSlotEmptiedEvent;
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
        Balm.networking().sendToServer(new MessageTrashSlotClick(ItemStack.EMPTY, isRightClick));
        ItemUntrashedEvent.EVENT.invoker().accept(new ItemUntrashedEvent(player, mouseStack));
    }

    @Override
    public boolean canUndeleteLast() {
        return true;
    }

    @Override
    public void deleteMouseItem(Player player, ItemStack mouseItem, TrashSlotSlot trashSlot, boolean isRightClick) {
        ItemStack mouseStack = mouseItem.copy();
        ItemStack trashStack = isRightClick ? mouseStack.split(1) : mouseStack;
        final var preEvent = new ItemTrashedEvent.Pre(player, trashStack);
        ItemTrashedEvent.Pre.EVENT.invoker().accept(preEvent);
        if (preEvent.isCanceled()) {
            return;
        }
        player.containerMenu.setCarried(isRightClick ? mouseStack : ItemStack.EMPTY);
        trashSlot.set(trashStack);
        Balm.networking().sendToServer(new MessageTrashSlotClick(mouseItem, isRightClick));
        ItemTrashedEvent.Post.EVENT.invoker().accept(new ItemTrashedEvent.Post(player, trashStack));
    }

    @Override
    public void deleteContainerItem(Player player, AbstractContainerMenu container, int slotNumber, boolean isDeleteAll, TrashSlotSlot slotTrash) {
        final var itemStack = container.getSlot(slotNumber).getItem();
        final var preEvent = new ItemTrashedEvent.Pre(player, itemStack);
        ItemTrashedEvent.Pre.EVENT.invoker().accept(preEvent);
        if (preEvent.isCanceled()) {
            return;
        }

        Balm.networking().sendToServer(new MessageDeleteFromSlot(slotNumber, isDeleteAll));
        ItemTrashedEvent.Post.EVENT.invoker().accept(new ItemTrashedEvent.Post(player, itemStack));
    }

    @Override
    public void emptyTrashSlot(Player player, TrashSlotSlot trashSlot) {
        final var itemStack = trashSlot.getItem();
        trashSlot.set(ItemStack.EMPTY);
        Balm.networking().sendToServer(new MessageDeleteFromSlot(-1, false));
        TrashSlotEmptiedEvent.EVENT.invoker().accept(new TrashSlotEmptiedEvent(player, itemStack));
    }
}
