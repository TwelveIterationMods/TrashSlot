package net.blay09.mods.trashslot;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.platform.config.reflection.Comment;
import net.blay09.mods.balm.platform.config.reflection.Config;
import net.blay09.mods.balm.platform.config.reflection.NestedType;
import net.blay09.mods.balm.platform.event.callback.ConfigCallback;
import net.blay09.mods.trashslot.client.deletion.CreativeDeletionProvider;
import net.blay09.mods.trashslot.client.deletion.DefaultDeletionProvider;
import net.blay09.mods.trashslot.client.deletion.DeletionProvider;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Config(TrashSlot.MOD_ID)
public class TrashSlotConfig {

    @Comment("This causes the deletion slot to delete items instantly, similar to Creative Mode.")
    public boolean instantDeletion = false;

    @Comment("Set to true if you want the delete keybindings to work in creative as well. Note: Items will be deleted permanently in creative, regardless of instantDeletion setting!")
    public boolean enableDeleteKeysInCreative = true;

    @Comment("TrashSlot will show a hint the first time the trash slot is toggled off or can be enabled on a supported screen. Set to false to disable.")
    public boolean enableHints = true;

    @Comment("Not recommended, but this will allow you to use the keybinds for deleting items even if the trash slot itself is hidden.")
    public boolean allowDeletionWhileTrashSlotIsInvisible = false;

    @Comment("List of items that cannot be deleted, in comma-separated format of \"modid:name\".")
    @NestedType(String.class)
    public List<String> deletionDenyList = new ArrayList<>();

    private static DeletionProvider deletionProvider;
    private static final DeletionProvider creativeDeletionProvider = new CreativeDeletionProvider();

    public static TrashSlotConfig getActive() {
        return Balm.config().getActiveConfig(TrashSlotConfig.class);
    }

    public static void initialize() {
        Balm.config().registerConfig(TrashSlotConfig.class);

        ConfigCallback.Reloaded.EVENT.register(schema -> deletionProvider = null);
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
