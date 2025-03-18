package net.blay09.mods.trashslot.network;

import net.blay09.mods.trashslot.client.TrashSlotClient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class MessageTrashSlotContent {

    private final ItemStack itemStack;

    public MessageTrashSlotContent(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public static void encode(MessageTrashSlotContent message, FriendlyByteBuf buf) {
        buf.writeItem(message.itemStack);
    }

    public static MessageTrashSlotContent decode(FriendlyByteBuf buf) {
        ItemStack itemStack = buf.readItem();
        return new MessageTrashSlotContent(itemStack);
    }

    public static void handle(Player player, MessageTrashSlotContent message) {
        TrashSlotClient.receivedTrashSlotContent(message.itemStack);
    }

}
