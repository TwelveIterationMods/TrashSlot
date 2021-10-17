package net.blay09.mods.trashslot;

import net.blay09.mods.trashslot.client.TrashSlotClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

@Mod(TrashSlot.MOD_ID)
public class ForgeTrashSlot {

    public ForgeTrashSlot() {
        TrashSlot.initialize();
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> TrashSlotClient::initialize);
    }

}
