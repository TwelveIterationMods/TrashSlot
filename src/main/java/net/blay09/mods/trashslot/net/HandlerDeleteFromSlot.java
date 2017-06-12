package net.blay09.mods.trashslot.net;

import net.blay09.mods.trashslot.TrashHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nullable;

public class HandlerDeleteFromSlot implements IMessageHandler<MessageDeleteFromSlot, IMessage> {

	@Override
	@Nullable
	public IMessage onMessage(final MessageDeleteFromSlot message, final MessageContext ctx) {
		NetworkHandler.getThreadListener(ctx).addScheduledTask(() -> {
			EntityPlayer player = ctx.getServerHandler().player;
			if (message.getSlotNumber() == -1) {
				TrashHelper.setTrashItem(player, ItemStack.EMPTY);
				return;
			}
			if (!player.inventory.getItemStack().isEmpty()) {
				return;
			}
			Container container = player.openContainer;
			Slot deleteSlot = container.inventorySlots.get(message.getSlotNumber());
			if(deleteSlot instanceof SlotCrafting) {
				return;
			}
			if (message.isShiftDown()) {
				ItemStack deleteStack = deleteSlot.getStack().copy();
				if(!deleteStack.isEmpty()) {
					if(attemptDeleteFromSlot(player, container, message.getSlotNumber())) {
						for (int i = 0; i < container.inventorySlots.size(); i++) {
							ItemStack slotStack = container.inventorySlots.get(i).getStack();
							if (!slotStack.isEmpty()
									&& ItemStack.areItemsEqualIgnoreDurability(slotStack, deleteStack)
									&& ItemStack.areItemStackTagsEqual(slotStack, deleteStack)) {
								if (!attemptDeleteFromSlot(player, container, i)) {
									break;
								}
							}
						}
					}
				}
			} else {
				attemptDeleteFromSlot(player, container, message.getSlotNumber());
			}
			NetworkHandler.instance.sendTo(new MessageTrashSlotContent(TrashHelper.getTrashItem(player)), (EntityPlayerMP) player);
		});
		return null;
	}

	private boolean attemptDeleteFromSlot(EntityPlayer player, Container container, int slotNumber) {
		ItemStack itemStack = container.slotClick(slotNumber, 0, ClickType.PICKUP, player);
		ItemStack mouseStack = player.inventory.getItemStack();
		if (ItemStack.areItemStacksEqual(itemStack, mouseStack)) {
			player.inventory.setItemStack(ItemStack.EMPTY);
			TrashHelper.setTrashItem(player, mouseStack);
			return !itemStack.isEmpty();
		} else {
			// Abort mission - something went weirdly wrong - sync the current mouse item to prevent desyncs
			((EntityPlayerMP) player).connection.sendPacket(new SPacketSetSlot(-1, 0, mouseStack));
			return false;
		}
	}

}
