package net.blay09.mods.trashslot.network;

import net.blay09.mods.trashslot.TrashHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class MessageTrashSlotClick {

    private final ItemStack itemStack;
    private final boolean isRightClick;

    public MessageTrashSlotClick(ItemStack itemStack, boolean isRightClick) {
        this.itemStack = itemStack;
        this.isRightClick = isRightClick;
    }

    public static void encode(MessageTrashSlotClick message, FriendlyByteBuf buf) {
        buf.writeItem(message.itemStack);
        buf.writeBoolean(message.isRightClick);
    }

    public static MessageTrashSlotClick decode(FriendlyByteBuf buf) {
        ItemStack itemStack = buf.readItem();
        boolean isRightClick = buf.readBoolean();
        return new MessageTrashSlotClick(itemStack, isRightClick);
    }

    public static void handle(ServerPlayer player, MessageTrashSlotClick message) {
        ItemStack actualMouseItem = player.containerMenu.getCarried();
        if (ItemStack.matches(actualMouseItem, message.itemStack)) {
            if (actualMouseItem.isEmpty()) {
                ItemStack trashStack = TrashHelper.getTrashItem(player);
                ItemStack mouseStack = message.isRightClick ? trashStack.split(1) : trashStack;
                player.containerMenu.setCarried(mouseStack);
                TrashHelper.setTrashItem(player, message.isRightClick ? trashStack : ItemStack.EMPTY);
            } else {
                ItemStack trashStack = message.isRightClick ? actualMouseItem.split(1) : actualMouseItem;
                TrashHelper.setTrashItem(player, trashStack);
                player.containerMenu.setCarried(message.isRightClick ? actualMouseItem : ItemStack.EMPTY);
            }
        }
    }

}
