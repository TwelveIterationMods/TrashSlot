package net.blay09.mods.trashslot.network;

import net.blay09.mods.trashslot.TrashHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class TrashSlotClickMessage {

    private final ItemStack itemStack;
    private final boolean isRightClick;

    public TrashSlotClickMessage(ItemStack itemStack, boolean isRightClick) {
        this.itemStack = itemStack;
        this.isRightClick = isRightClick;
    }

    public static void encode(TrashSlotClickMessage message, FriendlyByteBuf buf) {
        buf.writeItem(message.itemStack);
        buf.writeBoolean(message.isRightClick);
    }

    public static TrashSlotClickMessage decode(FriendlyByteBuf buf) {
        ItemStack itemStack = buf.readItem();
        boolean isRightClick = buf.readBoolean();
        return new TrashSlotClickMessage(itemStack, isRightClick);
    }

    public static void handle(ServerPlayer player, TrashSlotClickMessage message) {
        if (player == null) {
            return;
        }

        AbstractContainerMenu menu = player.containerMenu;
        ItemStack mouseItem = menu.getCarried();
        if (ItemStack.isSame(mouseItem, message.itemStack)) {
            if (mouseItem.isEmpty()) {
                ItemStack trashStack = TrashHelper.getTrashItem(player);
                ItemStack mouseStack = message.isRightClick ? trashStack.split(1) : trashStack;
                menu.setCarried(mouseStack);
                TrashHelper.setTrashItem(player, message.isRightClick ? trashStack : ItemStack.EMPTY);
            } else {
                ItemStack trashStack = message.isRightClick ? mouseItem.split(1) : mouseItem;
                TrashHelper.setTrashItem(player, trashStack);
                menu.setCarried(message.isRightClick ? mouseItem : ItemStack.EMPTY);
            }
        }
    }

}
