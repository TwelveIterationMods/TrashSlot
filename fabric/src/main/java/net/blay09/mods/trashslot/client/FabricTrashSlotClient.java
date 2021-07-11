package net.blay09.mods.trashslot.client;

import net.fabricmc.api.ClientModInitializer;

public class FabricTrashSlotClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        TrashSlotClient.initialize();
    }
}
