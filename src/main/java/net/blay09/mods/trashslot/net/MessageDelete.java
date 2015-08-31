package net.blay09.mods.trashslot.net;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class MessageDelete implements IMessage {

    private int slotNumber;
    private boolean shiftDown;

    public MessageDelete() {
    }

    public MessageDelete(int slotNumber, boolean shiftDown) {
        this.slotNumber = slotNumber;
        this.shiftDown = shiftDown;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        slotNumber = buf.readByte();
        shiftDown = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(slotNumber);
        buf.writeBoolean(shiftDown);
    }

    public int getSlotNumber() {
        return slotNumber;
    }

    public boolean isShiftDown() {
        return shiftDown;
    }
}
