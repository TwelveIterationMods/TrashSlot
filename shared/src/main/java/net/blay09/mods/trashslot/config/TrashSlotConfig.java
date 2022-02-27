package net.blay09.mods.trashslot.config;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.event.ConfigReloadedEvent;
import net.blay09.mods.trashslot.TrashSlot;
import net.blay09.mods.trashslot.client.deletion.CreativeDeletionProvider;
import net.blay09.mods.trashslot.client.deletion.DefaultDeletionProvider;
import net.blay09.mods.trashslot.client.deletion.DeletionProvider;
import org.jetbrains.annotations.Nullable;

public class TrashSlotConfig {

    private static DeletionProvider deletionProvider;
    private static final DeletionProvider creativeDeletionProvider = new CreativeDeletionProvider();

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
            deletionProvider = getActive().instantDeletion ? creativeDeletionProvider : new DefaultDeletionProvider();
        }

        return deletionProvider;
    }

    public static DeletionProvider getCreativeDeletionProvider() {
        return creativeDeletionProvider;
    }
}
