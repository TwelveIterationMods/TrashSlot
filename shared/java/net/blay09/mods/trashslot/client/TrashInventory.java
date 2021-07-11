package net.blay09.mods.trashslot.client;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class TrashInventory implements Container {
    private ItemStack currentStack = ItemStack.EMPTY;

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return currentStack.isEmpty();
    }

    @Override
    public ItemStack getItem(int i) {
        return currentStack;
    }

    @Override
    public ItemStack removeItem(int i, int count) {
        ItemStack itemStack = !currentStack.isEmpty() && count > 0 ? currentStack.split(count) : ItemStack.EMPTY;
        if (!itemStack.isEmpty()) {
            this.setChanged();
        }
        return itemStack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int i) {
        ItemStack itemStack = currentStack;
        currentStack = ItemStack.EMPTY;
        return itemStack;
    }

    @Override
    public void setItem(int i, ItemStack itemStack) {
        currentStack = itemStack;
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public void setChanged() {
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void startOpen(Player player) {
    }

    @Override
    public void stopOpen(Player player) {
    }

    @Override
    public void clearContent() {
        currentStack = ItemStack.EMPTY;
    }

    @Override
    public boolean canPlaceItem(int i, ItemStack itemStack) {
        return true;
    }

}
