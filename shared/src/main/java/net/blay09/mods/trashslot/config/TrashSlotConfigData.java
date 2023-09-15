package net.blay09.mods.trashslot.config;

import net.blay09.mods.balm.api.config.BalmConfigData;
import net.blay09.mods.balm.api.config.Comment;
import net.blay09.mods.balm.api.config.Config;
import net.blay09.mods.trashslot.TrashSlot;

@Config(TrashSlot.MOD_ID)
public class TrashSlotConfigData implements BalmConfigData {

    @Comment("This causes the deletion slot to delete items instantly, similar to Creative Mode.")
    public boolean instantDeletion = false;

    @Comment("Set to true if you want the delete keybindings to work in creative as well. Note: Items will be deleted permanently in creative, regardless of instantDeletion setting!")
    public boolean enableDeleteKeysInCreative = true;

    @Comment("TrashSlot will show a hint the first time the trash slot is toggled off or can be enabled on a supported screen. Set to false to disable.")
    public boolean enableHints = true;
}
