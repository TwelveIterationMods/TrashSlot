package net.blay09.mods.trashslot.config;

import me.shedaniel.autoconfig.annotation.Config;
import net.blay09.mods.balm.api.config.BalmConfigData;
import net.blay09.mods.balm.api.config.Comment;
import net.blay09.mods.trashslot.TrashSlot;

@Config(name = TrashSlot.MOD_ID)
public class TrashSlotConfigData implements BalmConfigData {

    @Comment("This causes the deletion slot to delete items instantly, similar to Creative Mode.")
    public boolean instantDeletion = false;

    @Comment("Set to true if you want the delete keybindings to work in creative as well. Note: Items will be deleted permanently in creative, regardless of instantDeletion setting!")
    public boolean enableDeleteKeysInCreative = true;
}
