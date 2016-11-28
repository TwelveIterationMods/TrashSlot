package net.blay09.mods.trashslot.net;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageDeleteFromSlot implements IMessage {

    private int slotNumber;
    private boolean isShiftDown;

    public MessageDeleteFromSlot() {
    }

    public MessageDeleteFromSlot(int slotNumber, boolean isShiftDown) {
        this.slotNumber = slotNumber;
        this.isShiftDown = isShiftDown;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        slotNumber = buf.readByte();
        isShiftDown = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(slotNumber);
        buf.writeBoolean(isShiftDown);
    }

    public int getSlotNumber() {
        return slotNumber;
    }

    public boolean isShiftDown() {
        return isShiftDown;
    }
}
