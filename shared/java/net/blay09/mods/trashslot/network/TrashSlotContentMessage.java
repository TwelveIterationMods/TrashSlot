package net.blay09.mods.trashslot.network;

import net.blay09.mods.trashslot.TrashSlot;
import net.blay09.mods.trashslot.client.TrashSlotClient;
import net.blay09.mods.trashslot.client.TrashSlotWidgetHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class TrashSlotContentMessage {

    private final ItemStack itemStack;

    public TrashSlotContentMessage(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public static void encode(TrashSlotContentMessage message, FriendlyByteBuf buf) {
        buf.writeItem(message.itemStack);
    }

    public static TrashSlotContentMessage decode(FriendlyByteBuf buf) {
        ItemStack itemStack = buf.readItem();
        return new TrashSlotContentMessage(itemStack);
    }

    public static void handle(Player player, TrashSlotContentMessage message) {
        TrashSlotWidgetHandler.getSlot().set(message.itemStack);
    }

}
