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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TrashSlotSaveState {

    private static final Logger logger = LogManager.getLogger();
    private static final String SETTINGS_FILE = "TrashSlotSaveState.json";
    private static final Gson gson = new Gson();
    private static final Set<String> hardcodedGuiBlackList = Sets.newHashSet();
    private static TrashSlotSaveState instance;

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
        return saveState.settingsMap.computeIfAbsent(containerId, it -> new ContainerSettings(layout.getDefaultSlotX(gui), layout.getDefaultSlotY(gui), 0.5f, 0.5f, layout.isEnabledByDefault()));
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(new File(Minecraft.getInstance().gameDirectory, SETTINGS_FILE))) {
            gson.toJson(instance, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static TrashSlotSaveState getInstance() {
        if (instance == null) {
            File saveStateFile = new File(Minecraft.getInstance().gameDirectory, SETTINGS_FILE);
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

    private Map<String, ContainerSettings> settingsMap = new HashMap<>();

    public Map<String, ContainerSettings> getSettingsMap() {
        return settingsMap;
    }
}
