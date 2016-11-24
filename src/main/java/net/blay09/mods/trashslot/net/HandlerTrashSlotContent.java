package net.blay09.mods.trashslot.net;

import net.blay09.mods.trashslot.TrashSlot;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nullable;

public class HandlerTrashSlotContent implements IMessageHandler<MessageTrashSlotContent, IMessage> {

	@Override
	@Nullable
	public IMessage onMessage(final MessageTrashSlotContent message, MessageContext ctx) {
		NetworkHandler.getThreadListener(ctx).addScheduledTask(new Runnable() {
			@Override
			public void run() {
				TrashSlot.proxy.getTrashSlot().putStack(message.getItemStack());
			}
		});
		return null;
	}

}
