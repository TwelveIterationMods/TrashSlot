package net.blay09.mods.trashslot.network;

import net.blay09.mods.trashslot.TrashHelper;
import net.blay09.mods.trashslot.TrashSlot;
import net.blay09.mods.trashslot.api.ItemTrashedEvent;
import net.blay09.mods.trashslot.api.ItemUntrashedEvent;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public record MessageTrashSlotClick(ItemStack itemStack, boolean isRightClick) implements CustomPacketPayload {

    public static final Type<MessageTrashSlotClick> TYPE = new Type<>(Identifier.fromNamespaceAndPath(TrashSlot.MOD_ID, "trash_slot_click"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageTrashSlotClick> STREAM_CODEC = StreamCodec.composite(
            ItemStack.OPTIONAL_STREAM_CODEC,
            MessageTrashSlotClick::itemStack,
            ByteBufCodecs.BOOL,
            MessageTrashSlotClick::isRightClick,
            MessageTrashSlotClick::new);

    public static void handle(ServerPlayer player, MessageTrashSlotClick message) {
        if (player.isSpectator()) {
            return;
        }

        ItemStack actualMouseItem = player.containerMenu.getCarried().copy();
        if (!TrashHelper.canDelete(actualMouseItem)) {
            return;
        }

        if (ItemStack.matches(actualMouseItem, message.itemStack)) {
            if (actualMouseItem.isEmpty()) {
                ItemStack trashStack = TrashHelper.getTrashItem(player);
                ItemStack mouseStack = message.isRightClick ? trashStack.split(1) : trashStack;
                player.containerMenu.setCarried(mouseStack);
                TrashHelper.setTrashItem(player, message.isRightClick ? trashStack : ItemStack.EMPTY);
                ItemUntrashedEvent.EVENT.invoker().accept(new ItemUntrashedEvent(player, mouseStack));
            } else {
                ItemStack trashStack = message.isRightClick ? actualMouseItem.split(1) : actualMouseItem;
                final var preEvent = new ItemTrashedEvent.Pre(player, trashStack);
                ItemTrashedEvent.Pre.EVENT.invoker().accept(preEvent);
                if (preEvent.isCanceled()) {
                    return;
                }
                TrashHelper.setTrashItem(player, trashStack);
                player.containerMenu.setCarried(message.isRightClick ? actualMouseItem : ItemStack.EMPTY);
                ItemTrashedEvent.Post.EVENT.invoker().accept(new ItemTrashedEvent.Post(player, trashStack));
            }
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
