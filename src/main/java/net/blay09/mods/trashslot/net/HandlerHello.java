package net.blay09.mods.trashslot.net;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import net.blay09.mods.trashslot.TrashSlot;

public class HandlerHello implements IMessageHandler<MessageHello, IMessage> {

    @Override
    public IMessage onMessage(MessageHello message, MessageContext ctx) {
        TrashSlot.proxy.receivedHello(NetworkHandler.getPlayerEntity(ctx));
        return ctx.side == Side.CLIENT ? new MessageHello(NetworkHandler.PROTOCOL_VERSION) : null;
    }

}

