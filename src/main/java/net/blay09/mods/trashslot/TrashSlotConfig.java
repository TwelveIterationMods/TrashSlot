package net.blay09.mods.trashslot;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.blay09.mods.trashslot.api.IGuiContainerLayout;
import net.blay09.mods.trashslot.client.ContainerSettings;
import net.blay09.mods.trashslot.client.deletion.CreativeDeletionProvider;
import net.blay09.mods.trashslot.client.deletion.DefaultDeletionProvider;
import net.blay09.mods.trashslot.client.deletion.DeletionProvider;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

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

    public static ModConfig clientConfig;
    private static final Map<String, ContainerSettings> settingsMap = Maps.newHashMap();
    private static final Set<String> hardcodedGuiBlackList = Sets.newHashSet();
    private static DeletionProvider deletionProvider;

    static {
        hardcodedGuiBlackList.add("client.gui.slimeknights/tconstruct/tools/common/client/module/GuiTinkerTabs");
        hardcodedGuiBlackList.add("client.gui.slimeknights/tconstruct/tools/common/client/GuiCraftingStation");
        hardcodedGuiBlackList.add("client.gui.slimeknights/tconstruct/tools/common/client/GuiPatternChest");
        hardcodedGuiBlackList.add("client.gui.slimeknights/tconstruct/tools/common/client/module/GuiButtonsStencilTable");
        hardcodedGuiBlackList.add("client.gui.slimeknights/tconstruct/tools/common/client/GuiPartBuilder");
    }

    @OnlyIn(Dist.CLIENT)
    public static ContainerSettings getSettings(GuiContainer gui, IGuiContainerLayout layout) {
        String category = getConfigCategory(gui, layout);
        if (hardcodedGuiBlackList.contains(category)) {
            return ContainerSettings.NONE;
        }

        return settingsMap.computeIfAbsent(category, c -> new ContainerSettings(clientConfig, c, layout.getDefaultSlotX(gui), layout.getDefaultSlotY(gui), layout.isEnabledByDefault()));
    }

    @OnlyIn(Dist.CLIENT)
    private static String getConfigCategory(GuiContainer gui, IGuiContainerLayout layout) {
        return "client.gui." + layout.getContainerId(gui);
    }

    @SubscribeEvent
    public static void onConfig(ModConfig.ModConfigEvent event) {
        if (event.getConfig().getType() == ModConfig.Type.CLIENT) {
            clientConfig = event.getConfig();
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
