package net.blay09.mods.trashslot;

import net.blay09.mods.trashslot.client.deletion.CreativeDeletionProvider;
import net.blay09.mods.trashslot.client.deletion.DefaultDeletionProvider;
import net.blay09.mods.trashslot.client.deletion.DeletionProvider;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = TrashSlot.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TrashSlotConfig {

    static final ForgeConfigSpec clientSpec;
    public static final TrashSlotConfig.Client CLIENT;

    static {
        final Pair<TrashSlotConfig.Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(TrashSlotConfig.Client::new);
        clientSpec = specPair.getRight();
        CLIENT = specPair.getLeft();
    }

    public static class Client {

        public final ForgeConfigSpec.BooleanValue instantDeletion;

        Client(ForgeConfigSpec.Builder builder) {
            builder.comment("Client only settings").push("client");

            instantDeletion = builder
                    .comment("This causes the deletion slot to delete items instantly, similar to Creative Mode.")
                    .translation("trashslot.config.instantDeletion")
                    .define("instantDeletion", false);
        }
    }

    private static DeletionProvider deletionProvider;

    @SubscribeEvent
    public static void onConfig(ModConfig.ModConfigEvent event) {
        if (event.getConfig().getType() == ModConfig.Type.CLIENT) {
            deletionProvider = null;
        }
    }

    @Nullable
    public static DeletionProvider getDeletionProvider() {
        if (TrashSlot.isServerSideInstalled && deletionProvider == null) {
            deletionProvider = TrashSlotConfig.CLIENT.instantDeletion.get() ? new CreativeDeletionProvider() : new DefaultDeletionProvider();
        }

        return deletionProvider;
    }

}
