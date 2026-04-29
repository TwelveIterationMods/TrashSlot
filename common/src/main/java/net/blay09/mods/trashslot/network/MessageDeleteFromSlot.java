package net.blay09.mods.trashslot.network;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.trashslot.TrashHelper;
import net.blay09.mods.trashslot.TrashSlot;
import net.blay09.mods.trashslot.api.event.ItemTrashedEvent;
import net.blay09.mods.trashslot.api.event.TrashSlotEmptiedEvent;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public record MessageDeleteFromSlot(int slotNumber, boolean isDeleteAll) implements CustomPacketPayload {

    public static final Type<MessageDeleteFromSlot> TYPE = new Type<>(Identifier.fromNamespaceAndPath(TrashSlot.MOD_ID, "delete_from_slot"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageDeleteFromSlot> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            MessageDeleteFromSlot::slotNumber,
            ByteBufCodecs.BOOL,
            MessageDeleteFromSlot::isDeleteAll,
            MessageDeleteFromSlot::new);

    public static void handle(ServerPlayer player, MessageDeleteFromSlot message) {
        if (player.isSpectator()) {
            return;
        }

        if (message.slotNumber == -1) {
            final var itemStack = TrashHelper.getTrashItem(player);
            TrashHelper.setTrashItem(player, ItemStack.EMPTY);
            Balm.networking().reply(new MessageTrashSlotContent(ItemStack.EMPTY));
            TrashSlotEmptiedEvent.EVENT.invoker().accept(new TrashSlotEmptiedEvent(player, itemStack));
            return;
        }

        if (!player.containerMenu.getCarried().isEmpty()) {
            return;
        }

        AbstractContainerMenu container = player.containerMenu;
        if (!container.isValidSlotIndex(message.slotNumber)) {
            return;
        }

        Slot deleteSlot = container.getSlot(message.slotNumber);
        if (deleteSlot instanceof ResultSlot) {
            return;
        }

        if (message.isDeleteAll) {
            ItemStack deleteStack = deleteSlot.getItem().copy();
            if (!deleteStack.isEmpty()) {
                if (attemptDeleteFromSlot(player, container, message.slotNumber)) {
                    for (int i = 0; i < container.slots.size(); i++) {
                        ItemStack slotStack = container.slots.get(i).getItem();
                        if (!slotStack.isEmpty() && ItemStack.isSameItemSameComponents(slotStack, deleteStack)) {
                            if (!attemptDeleteFromSlot(player, container, i)) {
                                break;
                            }
                        }
                    }
                }
            }
        } else {
            attemptDeleteFromSlot(player, container, message.slotNumber);
        }

        Balm.networking().reply(new MessageTrashSlotContent(TrashHelper.getTrashItem(player)));
    }

    private static boolean attemptDeleteFromSlot(Player player, AbstractContainerMenu container, int slotNumber) {
        ItemStack itemStack = container.getSlot(slotNumber).getItem().copy();
        if (!TrashHelper.canDelete(itemStack)) {
            return false;
        }

        container.clicked(slotNumber, 0, ContainerInput.PICKUP, player);
        ItemStack mouseStack = container.getCarried();
        final var preEvent = new ItemTrashedEvent.Pre(player, mouseStack);
        ItemTrashedEvent.Pre.EVENT.invoker().accept(preEvent);
        if (!preEvent.isCanceled() && ItemStack.matches(itemStack, mouseStack)) {
            container.setCarried(ItemStack.EMPTY);
            TrashHelper.setTrashItem(player, mouseStack);
            ItemTrashedEvent.Post.EVENT.invoker().accept(new ItemTrashedEvent.Post(player, mouseStack));
            return !itemStack.isEmpty();
        } else {
            // Abort mission - something went weirdly wrong - sync the current mouse item to prevent desyncs
            ((ServerPlayer) player).connection.send(new ClientboundContainerSetSlotPacket(-1, 0, 0, mouseStack));
            return false;
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
