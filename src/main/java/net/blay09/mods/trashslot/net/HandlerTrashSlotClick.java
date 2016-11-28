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
		NetworkHandler.getThreadListener(ctx).addScheduledTask(() -> {
			EntityPlayer player = ctx.getServerHandler().playerEntity;
			ItemStack actualMouseItem = player.inventory.getItemStack();
			if (ItemStack.areItemStacksEqual(actualMouseItem, message.getItemStack())) {
				if (actualMouseItem.isEmpty()) {
					ItemStack trashStack = TrashHelper.getTrashItem(player);
					ItemStack mouseStack = message.isRightClick() ? trashStack.splitStack(1) : trashStack;
					player.inventory.setItemStack(mouseStack);
					TrashHelper.setTrashItem(player, message.isRightClick() ? trashStack : ItemStack.EMPTY);
				} else {
					ItemStack trashStack = message.isRightClick() ? actualMouseItem.splitStack(1) : actualMouseItem;
					TrashHelper.setTrashItem(player, trashStack);
					player.inventory.setItemStack(message.isRightClick() ? actualMouseItem : ItemStack.EMPTY);
				}
			}
		});
		return null;
	}

}
