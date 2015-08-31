package net.blay09.mods.trashslot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotTrash extends Slot {

    private final EntityPlayer entityPlayer;
    private ItemStack itemStack;

    public SlotTrash(EntityPlayer entityPlayer, int x, int y) {
        super(null, 0, x, y);
        this.entityPlayer = entityPlayer;

        setBackgroundIcon(TrashSlot.proxy.getSlotBackgroundIcon());
    }

    @Override
    public ItemStack getStack() {
        return itemStack;
    }

    @Override
    public boolean getHasStack() {
        return itemStack != null;
    }

    @Override
    public void putStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public int getSlotStackLimit() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void onSlotChanged() {
        if(itemStack != null) {
            entityPlayer.inventory.setItemStack(null);
        }
    }

    @Override
    public boolean isItemValid(ItemStack itemStack) {
        return true;
    }

    @Override
    public boolean canTakeStack(EntityPlayer player) {
        return true;
    }

    @Override
    public ItemStack decrStackSize(int amount) {
        if(itemStack != null) {
            ItemStack returnStack;
            if(itemStack.stackSize <= amount) {
                returnStack = itemStack;
                itemStack = null;
                return returnStack;
            } else {
                returnStack = itemStack.splitStack(amount);
                if(itemStack.stackSize == 0) {
                    itemStack = null;
                }
                return returnStack;
            }
        }
        return null;
    }

    @Override
    public boolean isSlotInInventory(IInventory p_75217_1_, int p_75217_2_) {
        return false;
    }

}