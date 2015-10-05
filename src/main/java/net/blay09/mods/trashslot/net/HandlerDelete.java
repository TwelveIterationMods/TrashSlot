package net.blay09.mods.trashslot.net;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.blay09.mods.trashslot.SlotTrash;
import net.blay09.mods.trashslot.TrashSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class HandlerDelete implements IMessageHandler<MessageDelete, IMessage> {

    @Override
    public IMessage onMessage(MessageDelete message, MessageContext ctx) {
        EntityPlayer entityPlayer = ctx.getServerHandler().playerEntity;
        if(entityPlayer.openContainer == entityPlayer.inventoryContainer) {
            ItemStack trashItem = null;
            Slot deleteSlot = (Slot) entityPlayer.openContainer.inventorySlots.get(message.getSlotNumber());
            if(deleteSlot instanceof SlotTrash) {
                deleteSlot.putStack(null);
                return null;
            }
            if(message.isShiftDown()) {
                ItemStack deleteStack = deleteSlot.getStack();
                if(deleteStack != null) {
                    for(int i = 0; i < entityPlayer.inventory.getSizeInventory() - 4; i++) {
                        ItemStack slotStack = entityPlayer.inventory.getStackInSlot(i);
                        if(slotStack != null && ((deleteStack.getHasSubtypes() && deleteStack.isItemEqual(slotStack)) || deleteStack.getItem() == slotStack.getItem())) {
                            if(ItemStack.areItemStackTagsEqual(deleteStack, slotStack)) {
                                trashItem = slotStack;
                                entityPlayer.inventory.setInventorySlotContents(i, null);
                            }
                        }
                    }
                }
            } else {
                trashItem = deleteSlot.getStack();
                deleteSlot.putStack(null);
            }
            Slot slotTrash = TrashSlot.proxy.findSlotTrash(entityPlayer.inventoryContainer);
            slotTrash.putStack(trashItem);
        }
        return null;
    }

}
