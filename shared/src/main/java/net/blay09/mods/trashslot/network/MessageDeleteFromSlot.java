package net.blay09.mods.trashslot.network;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.trashslot.TrashHelper;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class MessageDeleteFromSlot {

    private final int slotNumber;
    private final boolean isDeleteAll;

    public MessageDeleteFromSlot(int slotNumber, boolean isDeleteAll) {
        this.slotNumber = slotNumber;
        this.isDeleteAll = isDeleteAll;
    }

    public static void encode(final MessageDeleteFromSlot message, final FriendlyByteBuf buf) {
        buf.writeVarInt(message.slotNumber);
        buf.writeBoolean(message.isDeleteAll);
    }

    public static MessageDeleteFromSlot decode(final FriendlyByteBuf buf) {
        int slotNumber = buf.readVarInt();
        boolean isDeleteAll = buf.readBoolean();
        return new MessageDeleteFromSlot(slotNumber, isDeleteAll);
    }

    public static void handle(ServerPlayer player, MessageDeleteFromSlot message) {
        if (player.isSpectator()) {
            return;
        }

        if (message.slotNumber == -1) {
            TrashHelper.setTrashItem(player, ItemStack.EMPTY);
            Balm.getNetworking().reply(new MessageTrashSlotContent(ItemStack.EMPTY));
            return;
        }

        if (!player.containerMenu.getCarried().isEmpty()) {
            return;
        }

        AbstractContainerMenu container = player.containerMenu;
        Slot deleteSlot = container.slots.get(message.slotNumber);
        if (deleteSlot instanceof ResultSlot) {
            return;
        }

        if (message.isDeleteAll) {
            ItemStack deleteStack = deleteSlot.getItem().copy();
            if (!deleteStack.isEmpty()) {
                if (attemptDeleteFromSlot(player, container, message.slotNumber)) {
                    for (int i = 0; i < container.slots.size(); i++) {
                        ItemStack slotStack = container.slots.get(i).getItem();
                        if (!slotStack.isEmpty() && ItemStack.isSameItemSameTags(slotStack, deleteStack)) {
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

        Balm.getNetworking().reply(new MessageTrashSlotContent(TrashHelper.getTrashItem(player)));
    }

    private static boolean attemptDeleteFromSlot(Player player, AbstractContainerMenu container, int slotNumber) {
        ItemStack itemStack = container.slots.get(slotNumber).getItem().copy();
        container.clicked(slotNumber, 0, ClickType.PICKUP, player);
        ItemStack mouseStack = container.getCarried();
        if (ItemStack.matches(itemStack, mouseStack)) {
            container.setCarried(ItemStack.EMPTY);
            TrashHelper.setTrashItem(player, mouseStack);
            return !itemStack.isEmpty();
        } else {
            // Abort mission - something went weirdly wrong - sync the current mouse item to prevent desyncs
            ((ServerPlayer) player).connection.send(new ClientboundContainerSetSlotPacket(-1, 0, 0, mouseStack));
            return false;
        }
    }
}
