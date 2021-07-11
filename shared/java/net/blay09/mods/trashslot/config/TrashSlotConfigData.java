package net.blay09.mods.trashslot.config;

import me.shedaniel.autoconfig.annotation.Config;
import net.blay09.mods.balm.config.BalmConfig;
import net.blay09.mods.balm.config.Comment;
import net.blay09.mods.trashslot.TrashSlot;
import net.blay09.mods.trashslot.client.deletion.CreativeDeletionProvider;
import net.blay09.mods.trashslot.client.deletion.DefaultDeletionProvider;
import net.blay09.mods.trashslot.client.deletion.DeletionProvider;

@Config(name = TrashSlot.MOD_ID)
public class TrashSlotConfigData extends BalmConfig {

    @Comment("This causes the deletion slot to delete items instantly, similar to Creative Mode.")
    public boolean instantDeletion = false;

    public DeletionProvider getDeletionProvider() {
        if (TrashSlot.isServerSideInstalled) {
            return instantDeletion ? new CreativeDeletionProvider() : new DefaultDeletionProvider();
        }

        return null;
    }

}
