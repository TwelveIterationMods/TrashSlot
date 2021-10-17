package net.blay09.mods.trashslot;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.event.ConfigReloadedEvent;
import net.blay09.mods.trashslot.client.deletion.CreativeDeletionProvider;
import net.blay09.mods.trashslot.client.deletion.DefaultDeletionProvider;
import net.blay09.mods.trashslot.client.deletion.DeletionProvider;

import javax.annotation.Nullable;

public class TrashSlotConfig {

    private static DeletionProvider deletionProvider;

    public static TrashSlotConfigData getActive() {
        return Balm.getConfig().getActive(TrashSlotConfigData.class);
    }

    public static void initialize() {
        Balm.getConfig().registerConfig(TrashSlotConfigData.class, null);

        Balm.getEvents().onEvent(ConfigReloadedEvent.class, event -> deletionProvider = null);
    }

    @Nullable
    public static DeletionProvider getDeletionProvider() {
        if (TrashSlot.isServerSideInstalled && deletionProvider == null) {
            deletionProvider = getActive().instantDeletion ? new CreativeDeletionProvider() : new DefaultDeletionProvider();
        }

        return deletionProvider;
    }

}
