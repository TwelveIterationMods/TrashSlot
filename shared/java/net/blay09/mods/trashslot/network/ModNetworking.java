package net.blay09.mods.trashslot.network;

import net.blay09.mods.balm.network.BalmNetworking;
import net.blay09.mods.trashslot.TrashSlot;
import net.minecraft.resources.ResourceLocation;

public class ModNetworking extends BalmNetworking {

    public static void initialize() {
// TODO            TrashSlot.isServerSideInstalled = it.equals("1.0");

        registerServerboundPacket(id("delete_from_slot"), DeleteFromSlotMessage.class, DeleteFromSlotMessage::encode, DeleteFromSlotMessage::decode, DeleteFromSlotMessage::handle);
        registerServerboundPacket(id("trash_slot_click"), TrashSlotClickMessage.class, TrashSlotClickMessage::encode, TrashSlotClickMessage::decode, TrashSlotClickMessage::handle);
        registerClientboundPacket(id("trash_slot_content"), TrashSlotContentMessage.class, TrashSlotContentMessage::encode, TrashSlotContentMessage::decode, TrashSlotContentMessage::handle);
    }

    private static ResourceLocation id(String name) {
        return new ResourceLocation(TrashSlot.MOD_ID, name);
    }

}
