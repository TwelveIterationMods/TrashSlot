package net.blay09.mods.trashslot.network;

import net.blay09.mods.trashslot.TrashHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.CraftingResultSlot;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageDeleteFromSlot {

    private final int slotNumber;
    private final boolean isShiftDown;

    public MessageDeleteFromSlot(int slotNumber, boolean isShiftDown) {
        this.slotNumber = slotNumber;
        this.isShiftDown = isShiftDown;
    }

    public static void encode(final MessageDeleteFromSlot message, final PacketBuffer buf) {
        buf.writeByte(message.slotNumber);
        buf.writeBoolean(message.isShiftDown);
    }

    public static MessageDeleteFromSlot decode(final PacketBuffer buf) {
        int slotNumber = buf.readByte();
        boolean isShiftDown = buf.readBoolean();
        return new MessageDeleteFromSlot(slotNumber, isShiftDown);
    }

    public static void handle(final MessageDeleteFromSlot message, final Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            PlayerEntity player = context.getSender();
            if (player == null) {
                return;
            }

            if (message.slotNumber == -1) {
                TrashHelper.setTrashItem(player, ItemStack.EMPTY);
                NetworkHandler.instance.reply(new MessageTrashSlotContent(ItemStack.EMPTY), context);
                return;
            }

            if (!player.inventory.getItemStack().isEmpty()) {
                return;
            }

            Container container = player.openContainer;
            Slot deleteSlot = container.inventorySlots.get(message.slotNumber);
            if (deleteSlot instanceof CraftingResultSlot) {
                return;
            }

            if (message.isShiftDown) {
                ItemStack deleteStack = deleteSlot.getStack().copy();
                if (!deleteStack.isEmpty()) {
                    if (attemptDeleteFromSlot(player, container, message.slotNumber)) {
                        for (int i = 0; i < container.inventorySlots.size(); i++) {
                            ItemStack slotStack = container.inventorySlots.get(i).getStack();
                            if (!slotStack.isEmpty()
                                    && ItemStack.areItemsEqualIgnoreDurability(slotStack, deleteStack)
                                    && ItemStack.areItemStackTagsEqual(slotStack, deleteStack)) {
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

            NetworkHandler.instance.reply(new MessageTrashSlotContent(TrashHelper.getTrashItem(player)), context);
        });
        context.setPacketHandled(true);
    }

    private static boolean attemptDeleteFromSlot(PlayerEntity player, Container container, int slotNumber) {
        ItemStack itemStack = container.slotClick(slotNumber, 0, ClickType.PICKUP, player);
        ItemStack mouseStack = player.inventory.getItemStack();
        if (ItemStack.areItemStacksEqual(itemStack, mouseStack)) {
            player.inventory.setItemStack(ItemStack.EMPTY);
            TrashHelper.setTrashItem(player, mouseStack);
            return !itemStack.isEmpty();
        } else {
            // Abort mission - something went weirdly wrong - sync the current mouse item to prevent desyncs
            ((ServerPlayerEntity) player).connection.sendPacket(new SSetSlotPacket(-1, 0, mouseStack));
            return false;
        }
    }
}
