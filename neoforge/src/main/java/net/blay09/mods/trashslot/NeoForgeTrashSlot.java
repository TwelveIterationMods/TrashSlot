package net.blay09.mods.trashslot;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.client.BalmClient;
import net.blay09.mods.trashslot.client.TrashSlotClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.DistExecutor;
import net.neoforged.fml.common.Mod;

@Mod(TrashSlot.MOD_ID)
public class NeoForgeTrashSlot {

    public NeoForgeTrashSlot() {
        PlatformBindings.INSTANCE = new PlatformBindings() {
            @Override
            public boolean supportsKeyModifiers() {
                return true;
            }
        };

        Balm.initialize(TrashSlot.MOD_ID, TrashSlot::initialize);
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> BalmClient.initialize(TrashSlot.MOD_ID, TrashSlotClient::initialize));
    }

}
