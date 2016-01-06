package net.blay09.mods.trashslot.net;

import net.blay09.mods.trashslot.TrashSlot;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class HandlerHello implements IMessageHandler<MessageHello, IMessage> {

    @Override
    public IMessage onMessage(MessageHello message, final MessageContext ctx) {
        TrashSlot.proxy.addScheduledTask(new Runnable() {
            @Override
            public void run() {
                TrashSlot.proxy.receivedHello(NetworkHandler.getPlayerEntity(ctx));
            }
        });
        return ctx.side == Side.CLIENT ? new MessageHello(NetworkHandler.PROTOCOL_VERSION) : null;
    }

}

