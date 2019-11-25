package net.blay09.mods.trashslot.network;

import net.blay09.mods.trashslot.TrashSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageTrashSlotContent {

    private final ItemStack itemStack;

    public MessageTrashSlotContent(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public static void encode(MessageTrashSlotContent message, PacketBuffer buf) {
        buf.writeItemStack(message.itemStack);
    }

    public static MessageTrashSlotContent decode(PacketBuffer buf) {
        ItemStack itemStack = buf.readItemStack();
        return new MessageTrashSlotContent(itemStack);
    }

    public static void handle(MessageTrashSlotContent message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> TrashSlot.trashSlotGui.ifPresent(it -> it.getTrashSlot().putStack(message.itemStack)));
        context.setPacketHandled(true);
    }

}
