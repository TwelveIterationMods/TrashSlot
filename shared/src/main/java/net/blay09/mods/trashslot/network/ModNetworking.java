package net.blay09.mods.trashslot.network;

import net.blay09.mods.balm.api.network.BalmNetworking;
import net.blay09.mods.trashslot.TrashSlot;
import net.minecraft.resources.ResourceLocation;

public class ModNetworking {

    public static void initialize(BalmNetworking networking) {
        networking.registerServerboundPacket(id("delete_from_slot"), MessageDeleteFromSlot.class, MessageDeleteFromSlot::encode, MessageDeleteFromSlot::decode, MessageDeleteFromSlot::handle);
        networking.registerServerboundPacket(id("click_trash_slot"), MessageTrashSlotClick.class, MessageTrashSlotClick::encode, MessageTrashSlotClick::decode, MessageTrashSlotClick::handle);
        networking.registerClientboundPacket(id("trash_slot_content"), MessageTrashSlotContent.class, MessageTrashSlotContent::encode, MessageTrashSlotContent::decode, MessageTrashSlotContent::handle);
    }

    private static ResourceLocation id(String path) {
        return new ResourceLocation(TrashSlot.MOD_ID, path);
    }
}
