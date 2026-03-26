package net.blay09.mods.trashslot.fabric;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.fabric.platform.runtime.FabricLoadContext;
import net.blay09.mods.trashslot.TrashSlot;
import net.fabricmc.api.ModInitializer;

public class FabricTrashSlot implements ModInitializer {
    @Override
    public void onInitialize() {
        PlatformBindings.INSTANCE = new PlatformBindings() {
            @Override
            public boolean supportsKeyModifiers() {
                return false;
            }
        };

        Balm.initializeMod(TrashSlot.MOD_ID, FabricLoadContext.INSTANCE, TrashSlot::initialize);
    }
}
