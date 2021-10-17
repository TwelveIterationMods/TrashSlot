package net.blay09.mods.trashslot;

import net.blay09.mods.balm.api.config.BalmConfigData;
import net.blay09.mods.balm.api.config.Comment;

public class TrashSlotConfigData implements BalmConfigData {

    @Comment("This causes the deletion slot to delete items instantly, similar to Creative Mode.")
    public boolean instantDeletion = false;

}
