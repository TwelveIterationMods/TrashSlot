package net.blay09.mods.trashslot.net;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nullable;

public class HandlerDelete implements IMessageHandler<MessageDelete, IMessage> {

    @Override
    @Nullable
    public IMessage onMessage(final MessageDelete message, final MessageContext ctx) {
        // TODO rewrite me
        NetworkHandler.getThreadListener(ctx).addScheduledTask(new Runnable() {
            @Override
            public void run() {
                EntityPlayer entityPlayer = ctx.getServerHandler().playerEntity;
                if(entityPlayer.openContainer == entityPlayer.inventoryContainer) {
                    ItemStack trashItem = ItemStack.EMPTY;
                    Slot deleteSlot = entityPlayer.openContainer.inventorySlots.get(message.getSlotNumber());
//                    if(deleteSlot instanceof SlotTrash) {
//                        deleteSlot.putStack(ItemStack.EMPTY);
//                        return;
//                    }
                    if(message.isShiftDown()) {
                        ItemStack deleteStack = deleteSlot.getStack();
                        if(!deleteStack.isEmpty()) {
                            for(int i = 0; i < entityPlayer.inventory.getSizeInventory() - 4; i++) {
                                ItemStack slotStack = entityPlayer.inventory.getStackInSlot(i);
                                if(!slotStack.isEmpty() && ((deleteStack.getHasSubtypes() && deleteStack.isItemEqual(slotStack)) || deleteStack.getItem() == slotStack.getItem())) {
                                    if(ItemStack.areItemStackTagsEqual(deleteStack, slotStack)) {
                                        trashItem = slotStack;
                                        entityPlayer.inventory.setInventorySlotContents(i, ItemStack.EMPTY);
                                    }
                                }
                            }
                        }
                    } else {
                        trashItem = deleteSlot.getStack();
                        deleteSlot.putStack(ItemStack.EMPTY);
                    }
                    // TODO putStack into trash slot
                }
            }
        });
        return null;
    }

}
