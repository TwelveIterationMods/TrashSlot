package net.blay09.mods.trashslot.fabric;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.EmptyLoadContext;
import net.blay09.mods.trashslot.PlatformBindings;
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

        Balm.initialize(TrashSlot.MOD_ID, EmptyLoadContext.INSTANCE, TrashSlot::initialize);
    }
}
