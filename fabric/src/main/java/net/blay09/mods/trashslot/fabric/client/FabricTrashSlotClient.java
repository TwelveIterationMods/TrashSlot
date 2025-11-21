package net.blay09.mods.trashslot.fabric.client;

import net.blay09.mods.balm.client.BalmClient;
import net.blay09.mods.balm.fabric.platform.runtime.FabricLoadContext;
import net.blay09.mods.trashslot.TrashSlot;
import net.blay09.mods.trashslot.client.TrashSlotClient;
import net.fabricmc.api.ClientModInitializer;

public class FabricTrashSlotClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BalmClient.initializeMod(TrashSlot.MOD_ID, FabricLoadContext.INSTANCE, TrashSlotClient::initialize);
    }
}
