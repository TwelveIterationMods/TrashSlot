package net.blay09.mods.trashslot;

import net.blay09.mods.balm.api.Balm;
import net.fabricmc.api.ModInitializer;

public class FabricTrashSlot implements ModInitializer {
    @Override
    public void onInitialize() {
        Balm.initialize(TrashSlot.MOD_ID, TrashSlot::initialize);
    }
}
