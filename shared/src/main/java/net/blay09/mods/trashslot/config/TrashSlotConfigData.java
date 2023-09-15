package net.blay09.mods.trashslot.config;

import me.shedaniel.autoconfig.annotation.Config;
import net.blay09.mods.balm.api.config.BalmConfigData;
import net.blay09.mods.balm.api.config.Comment;
import net.blay09.mods.balm.api.config.ExpectedType;
import net.blay09.mods.trashslot.TrashSlot;

import java.util.ArrayList;
import java.util.List;

@Config(name = TrashSlot.MOD_ID)
public class TrashSlotConfigData implements BalmConfigData {

    @Comment("This causes the deletion slot to delete items instantly, similar to Creative Mode.")
    public boolean instantDeletion = false;

    @Comment("Set to true if you want the delete keybindings to work in creative as well. Note: Items will be deleted permanently in creative, regardless of instantDeletion setting!")
    public boolean enableDeleteKeysInCreative = true;

    @Comment("TrashSlot will show a hint the first time the trash slot is toggled off or can be enabled on a supported screen. Set to false to disable.")
    public boolean enableHints = true;

    @Comment("Not recommended, but this will allow you to use the keybinds for deleting items even if the trash slot itself is hidden.")
    public boolean allowDeletionWhileTrashSlotIsInvisible = false;

    @Comment("List of items that cannot be deleted, in comma-separated format of \"modid:name\".")
    @ExpectedType(String.class)
    public List<String> deletionDenyList = new ArrayList<>();
}
