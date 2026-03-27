package net.blay09.mods.trashslot.neoforge;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.neoforge.platform.runtime.NeoForgeLoadContext;
import net.blay09.mods.trashslot.TrashSlot;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(TrashSlot.MOD_ID)
public class NeoForgeTrashSlot {

    public NeoForgeTrashSlot(ModContainer modContainer, IEventBus eventBus) {
        final var loadContext = new NeoForgeLoadContext(modContainer, eventBus);
        Balm.initializeMod(TrashSlot.MOD_ID, loadContext, TrashSlot::initialize);
    }

}
