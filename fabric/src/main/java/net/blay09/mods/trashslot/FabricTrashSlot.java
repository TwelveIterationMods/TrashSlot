package net.blay09.mods.trashslot;

import net.fabricmc.api.ModInitializer;

public class FabricTrashSlot implements ModInitializer {
    @Override
    public void onInitialize() {
        TrashSlot.initialize();
    }
}
