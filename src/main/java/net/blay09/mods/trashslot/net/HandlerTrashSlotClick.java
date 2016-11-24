package net.blay09.mods.trashslot.net;

import net.blay09.mods.trashslot.TrashHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nullable;

public class HandlerTrashSlotClick implements IMessageHandler<MessageTrashSlotClick, IMessage> {

	@Override
	@Nullable
	public IMessage onMessage(final MessageTrashSlotClick message, final MessageContext ctx) {
		NetworkHandler.getThreadListener(ctx).addScheduledTask(new Runnable() {
			@Override
			public void run() {
				EntityPlayer player = ctx.getServerHandler().playerEntity;
				ItemStack actualMouseItem = player.inventory.getItemStack();
				if(ItemStack.areItemStacksEqual(actualMouseItem, message.getItemStack())) {
					if(actualMouseItem.isEmpty()) {
						player.inventory.setItemStack(TrashHelper.getTrashItem(player));
						TrashHelper.setTrashItem(player, ItemStack.EMPTY);
					} else {
						TrashHelper.setTrashItem(player, actualMouseItem);
						player.inventory.setItemStack(ItemStack.EMPTY);
					}
				}

			}
		});
		return null;
	}

}
