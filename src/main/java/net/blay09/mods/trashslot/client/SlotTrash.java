package net.blay09.mods.trashslot.client;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class SlotTrash extends Slot {

    public static class TrashInventory implements IInventory {
        private ItemStack currentStack = ItemStack.EMPTY;

        @Override
        public int getSizeInventory() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return currentStack != null;
        }

        @Override
        public ItemStack getStackInSlot(int index) {
            return currentStack;
        }

        @Override
        public ItemStack decrStackSize(int index, int count) {
            ItemStack itemStack = !currentStack.isEmpty() && count > 0 ? currentStack.split(count) : ItemStack.EMPTY;
            if (!itemStack.isEmpty()) {
                this.markDirty();
            }
            return itemStack;
        }

        @Override
        public ItemStack removeStackFromSlot(int index) {
            ItemStack itemStack = currentStack;
            currentStack = ItemStack.EMPTY;
            return itemStack;
        }

        @Override
        public void setInventorySlotContents(int index, ItemStack stack) {
            currentStack = stack;
        }

        @Override
        public int getInventoryStackLimit() {
            return 64;
        }

        @Override
        public void markDirty() {
        }

        @Override
        public boolean isUsableByPlayer(PlayerEntity player) {
            return true;
        }

        @Override
        public void openInventory(PlayerEntity player) {
        }

        @Override
        public void closeInventory(PlayerEntity player) {
        }

        @Override
        public boolean isItemValidForSlot(int index, ItemStack stack) {
            return true;
        }

        @Override
        public void clear() {
            currentStack = null;
        }
    }

    public SlotTrash() {
        super(new TrashInventory(), 0, 0, 0);
        backgroundName = "trashslot:item/trashcan";
    }

}
