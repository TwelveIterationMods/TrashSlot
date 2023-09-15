package net.blay09.mods.trashslot;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import net.blay09.mods.trashslot.api.IGuiContainerLayout;
import net.blay09.mods.trashslot.client.ContainerSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TrashSlotSaveState {

    private static final Logger logger = LogManager.getLogger();
    private static final String SETTINGS_FILE = "TrashSlotSaveState.json";
    private static final String DEFAULT_SETTINGS_FILE = "TrashSlotSaveState.default.json";
    private static final Gson gson = new Gson();
    private static final Set<String> hardcodedGuiBlackList = Sets.newHashSet();
    private static TrashSlotSaveState instance;

    private final Set<String> hintsSeen = new HashSet<>();
    private final Map<String, ContainerSettings> cachedSettings = new HashMap<>();

    static {
        hardcodedGuiBlackList.add("slimeknights/tconstruct/tools/common/client/module/GuiTinkerTabs");
        hardcodedGuiBlackList.add("slimeknights/tconstruct/tools/common/client/GuiCraftingStation");
        hardcodedGuiBlackList.add("slimeknights/tconstruct/tools/common/client/GuiPatternChest");
        hardcodedGuiBlackList.add("slimeknights/tconstruct/tools/common/client/module/GuiButtonsStencilTable");
        hardcodedGuiBlackList.add("slimeknights/tconstruct/tools/common/client/GuiPartBuilder");
    }

    public static ContainerSettings getSettings(AbstractContainerScreen<?> gui, IGuiContainerLayout layout) {
        String containerId = layout.getContainerId(gui);
        if (hardcodedGuiBlackList.contains(containerId)) {
            return ContainerSettings.NONE;
        }

        TrashSlotSaveState saveState = getInstance();
        return saveState.cachedSettings.computeIfAbsent(containerId, it -> new ContainerSettings(layout.getDefaultSlotX(gui), layout.getDefaultSlotY(gui), 0.5f, 0.5f, layout.isEnabledByDefault()));
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(new File(Minecraft.getInstance().gameDirectory, SETTINGS_FILE))) {
            gson.toJson(instance, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean hasSeenHint(String hint) {
        return false && hintsSeen.contains(hint);
    }

    public void markHintAsSeen(String hint) {
        hintsSeen.add(hint);
    }

    public static TrashSlotSaveState getInstance() {
        if (instance == null) {
            File saveStateFile = new File(Minecraft.getInstance().gameDirectory, SETTINGS_FILE);
            File defaultSaveStateFile = new File(Minecraft.getInstance().gameDirectory, DEFAULT_SETTINGS_FILE);
            if (!saveStateFile.exists() && defaultSaveStateFile.exists()) {
                try {
                    Files.copy(defaultSaveStateFile.toPath(), saveStateFile.toPath());
                } catch (IOException e) {
                    logger.error("Failed to load TrashSlot default save state, will ignore defaults", e);
                }
            }

            if (saveStateFile.exists()) {
                try (FileReader reader = new FileReader(saveStateFile)) {
                    instance = gson.fromJson(reader, TrashSlotSaveState.class);
                } catch (Throwable e) {
                    logger.error("Failed to load TrashSlot save state, resetting to default", e);
                }
            }
        }

        if (instance == null) {
            instance = new TrashSlotSaveState();
        }

        return instance;
    }

}
