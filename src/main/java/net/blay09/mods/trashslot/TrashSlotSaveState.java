package net.blay09.mods.trashslot;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import net.blay09.mods.trashslot.api.IGuiContainerLayout;
import net.blay09.mods.trashslot.client.ContainerSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TrashSlotSaveState {

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

    public static ContainerSettings getSettings(ContainerScreen<?> gui, IGuiContainerLayout layout) {
        String containerId = layout.getContainerId(gui);
        if (hardcodedGuiBlackList.contains(containerId)) {
            return ContainerSettings.NONE;
        }

        TrashSlotSaveState saveState = getInstance();
        return saveState.settingsMap.computeIfAbsent(containerId, it -> new ContainerSettings(layout.getDefaultSlotX(gui), layout.getDefaultSlotY(gui), 0.5f, 0.5f, layout.isEnabledByDefault()));
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(new File(Minecraft.getInstance().gameDir, SETTINGS_FILE))) {
            gson.toJson(instance, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static TrashSlotSaveState getInstance() {
        if (instance == null) {
            File saveStateFile = new File(Minecraft.getInstance().gameDir, SETTINGS_FILE);
            if (saveStateFile.exists()) {
                try (FileReader reader = new FileReader(saveStateFile)) {
                    instance = gson.fromJson(reader, TrashSlotSaveState.class);
                } catch (IOException e) {
                    e.printStackTrace();
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
