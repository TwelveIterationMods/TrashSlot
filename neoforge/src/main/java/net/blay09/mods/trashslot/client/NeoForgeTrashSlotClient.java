package net.blay09.mods.trashslot.client;

import net.blay09.mods.balm.client.BalmClient;
import net.blay09.mods.balm.neoforge.platform.runtime.NeoForgeLoadContext;
import net.blay09.mods.trashslot.TrashSlot;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(value = TrashSlot.MOD_ID, dist = Dist.CLIENT)
public class NeoForgeTrashSlotClient {

    public NeoForgeTrashSlotClient(ModContainer modContainer, IEventBus eventBus) {
        final var loadContext = new NeoForgeLoadContext(modContainer, eventBus);
        BalmClient.initializeMod(TrashSlot.MOD_ID, loadContext, TrashSlotClient::initialize);
    }

}
