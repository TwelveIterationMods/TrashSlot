package net.blay09.mods.trashslot;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.neoforge.NeoForgeLoadContext;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(TrashSlot.MOD_ID)
public class NeoForgeTrashSlot {

    public NeoForgeTrashSlot(IEventBus eventBus) {
        PlatformBindings.INSTANCE = new PlatformBindings() {
            @Override
            public boolean supportsKeyModifiers() {
                return true;
            }
        };

        final var loadContext = new NeoForgeLoadContext(eventBus);
        Balm.initialize(TrashSlot.MOD_ID, loadContext, TrashSlot::initialize);
    }

}
