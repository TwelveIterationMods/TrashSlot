package net.blay09.mods.trashslot.net;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageTrashSlotContent implements IMessage {

	private ItemStack itemStack;

	public MessageTrashSlotContent() {
	}

	public MessageTrashSlotContent(ItemStack itemStack) {
		this.itemStack = itemStack;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		itemStack = ByteBufUtils.readItemStack(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeItemStack(buf, itemStack);
	}

	public ItemStack getItemStack() {
		return itemStack;
	}

}
