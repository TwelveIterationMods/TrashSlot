package net.blay09.mods.trashslot.network;

import net.blay09.mods.trashslot.TrashHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageTrashSlotClick {

    private final ItemStack itemStack;
    private final boolean isRightClick;

    public MessageTrashSlotClick(ItemStack itemStack, boolean isRightClick) {
        this.itemStack = itemStack;
        this.isRightClick = isRightClick;
    }

    public static void encode(MessageTrashSlotClick message, PacketBuffer buf) {
        buf.writeItemStack(message.itemStack);
        buf.writeBoolean(message.isRightClick);
    }

    public static MessageTrashSlotClick decode(PacketBuffer buf) {
        ItemStack itemStack = buf.readItemStack();
        boolean isRightClick = buf.readBoolean();
        return new MessageTrashSlotClick(itemStack, isRightClick);
    }

    public static void handle(MessageTrashSlotClick message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            PlayerEntity player = context.getSender();
            if (player == null) {
                return;
            }

            ItemStack actualMouseItem = player.inventory.getItemStack();
            if (ItemStack.areItemStacksEqual(actualMouseItem, message.itemStack)) {
                if (actualMouseItem.isEmpty()) {
                    ItemStack trashStack = TrashHelper.getTrashItem(player);
                    ItemStack mouseStack = message.isRightClick ? trashStack.split(1) : trashStack;
                    player.inventory.setItemStack(mouseStack);
                    TrashHelper.setTrashItem(player, message.isRightClick ? trashStack : ItemStack.EMPTY);
                } else {
                    ItemStack trashStack = message.isRightClick ? actualMouseItem.split(1) : actualMouseItem;
                    TrashHelper.setTrashItem(player, trashStack);
                    player.inventory.setItemStack(message.isRightClick ? actualMouseItem : ItemStack.EMPTY);
                }
            }
        });
        context.setPacketHandled(true);
    }

}
