package net.blay09.mods.trashslot.config;

import net.blay09.mods.balm.config.BalmConfigHolder;

public class TrashSlotConfig {

    public static void initialize() {
        BalmConfigHolder.registerConfig(TrashSlotConfigData.class, null);
    }

    public static TrashSlotConfigData getActive() {
        return BalmConfigHolder.getActive(TrashSlotConfigData.class);
    }
}
