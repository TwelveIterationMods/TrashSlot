package net.blay09.mods.trashslot.network;

import net.blay09.mods.balm.api.network.BalmNetworking;
import net.blay09.mods.trashslot.TrashSlot;

public class ModNetworking {

    public static void initialize(BalmNetworking networking) {
        networking.allowClientAndServerOnly(TrashSlot.MOD_ID);

        networking.registerServerboundPacket(MessageDeleteFromSlot.TYPE, MessageDeleteFromSlot.class, MessageDeleteFromSlot::encode, MessageDeleteFromSlot::decode, MessageDeleteFromSlot::handle);
        networking.registerServerboundPacket(MessageTrashSlotClick.TYPE, MessageTrashSlotClick.class, MessageTrashSlotClick::encode, MessageTrashSlotClick::decode, MessageTrashSlotClick::handle);
        networking.registerClientboundPacket(MessageTrashSlotContent.TYPE, MessageTrashSlotContent.class, MessageTrashSlotContent::encode, MessageTrashSlotContent::decode, MessageTrashSlotContent::handle);
    }

}
