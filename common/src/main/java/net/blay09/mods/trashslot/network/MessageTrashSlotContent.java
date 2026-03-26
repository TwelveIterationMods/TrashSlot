package net.blay09.mods.trashslot.network;

import net.blay09.mods.trashslot.TrashSlot;
import net.blay09.mods.trashslot.client.TrashSlotClient;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public record MessageTrashSlotContent(ItemStack itemStack) implements CustomPacketPayload {

    public static final Type<MessageTrashSlotContent> TYPE = new Type<>(Identifier.fromNamespaceAndPath(TrashSlot.MOD_ID, "trash_slot_content"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageTrashSlotContent> STREAM_CODEC = StreamCodec.composite(
            ItemStack.OPTIONAL_STREAM_CODEC,
            MessageTrashSlotContent::itemStack,
            MessageTrashSlotContent::new);

    public static void handle(Player player, MessageTrashSlotContent message) {
        TrashSlotClient.receivedTrashSlotContent(message.itemStack);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
