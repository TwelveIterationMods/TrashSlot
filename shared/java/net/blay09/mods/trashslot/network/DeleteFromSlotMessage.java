package net.blay09.mods.trashslot.network;

import net.blay09.mods.balm.network.BalmNetworking;
import net.blay09.mods.trashslot.TrashHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class DeleteFromSlotMessage {

    private final int slotNumber;
    private final boolean isShiftDown;

    public DeleteFromSlotMessage(int slotNumber, boolean isShiftDown) {
        this.slotNumber = slotNumber;
        this.isShiftDown = isShiftDown;
    }

    public static void encode(final DeleteFromSlotMessage message, final FriendlyByteBuf buf) {
        buf.writeByte(message.slotNumber);
        buf.writeBoolean(message.isShiftDown);
    }

    public static DeleteFromSlotMessage decode(final FriendlyByteBuf buf) {
        int slotNumber = buf.readByte();
        boolean isShiftDown = buf.readBoolean();
        return new DeleteFromSlotMessage(slotNumber, isShiftDown);
    }

    public static void handle(ServerPlayer player, DeleteFromSlotMessage message) {
        if (player == null) {
            return;
        }

        if (message.slotNumber == -1) {
            TrashHelper.setTrashItem(player, ItemStack.EMPTY);
            BalmNetworking.sendTo(player, new TrashSlotContentMessage(ItemStack.EMPTY));
            return;
        }

        AbstractContainerMenu menu = player.containerMenu;
        if (!menu.getCarried().isEmpty()) {
            return;
        }

        Slot deleteSlot = menu.slots.get(message.slotNumber);
        if (deleteSlot instanceof ResultSlot) {
            return;
        }

        if (message.isShiftDown) {
            ItemStack deleteStack = deleteSlot.getItem().copy();
            if (!deleteStack.isEmpty()) {
                if (attemptDeleteFromSlot(player, menu, message.slotNumber)) {
                    for (int i = 0; i < menu.slots.size(); i++) {
                        ItemStack slotStack = menu.slots.get(i).getItem();
                        if (!slotStack.isEmpty()
                                && ItemStack.isSameIgnoreDurability(slotStack, deleteStack)
                                && ItemStack.tagMatches(slotStack, deleteStack)) {
                            if (!attemptDeleteFromSlot(player, menu, i)) {
                                break;
                            }
                        }
                    }
                }
            }
        } else {
            attemptDeleteFromSlot(player, menu, message.slotNumber);
        }

        BalmNetworking.sendTo(player, new TrashSlotContentMessage(TrashHelper.getTrashItem(player)));
    }

    private static boolean attemptDeleteFromSlot(Player player, AbstractContainerMenu menu, int slotNumber) {
        ItemStack itemStack = menu.slots.get(slotNumber).getItem();
        menu.clicked(slotNumber, 0, ClickType.PICKUP, player);
        ItemStack mouseStack = menu.getCarried();
        if (ItemStack.isSame(itemStack, mouseStack)) {
            menu.setCarried(ItemStack.EMPTY);
            TrashHelper.setTrashItem(player, mouseStack);
            return !itemStack.isEmpty();
        } else {
            // Abort mission - something went weirdly wrong - sync the current mouse item to prevent desyncs
            ((ServerPlayer) player).connection.send(new ClientboundContainerSetSlotPacket(-1, menu.incrementStateId(), 0, mouseStack));
            return false;
        }
    }
}
