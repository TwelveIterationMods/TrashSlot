package net.blay09.mods.trashslot.client;

import com.electronwill.nightconfig.core.CommentedConfig;
import net.minecraftforge.fml.config.ModConfig;

public class ContainerSettings {

    public static final ContainerSettings NONE = new ContainerSettings() {
        @Override
        public void setEnabled(boolean isEnabled) {
        }
    };

    private final String category;
    private int slotX;
    private int slotY;
    private float anchorX;
    private float anchorY;
    private boolean isEnabled;

    private ContainerSettings() {
        category = "null";
    }

    public ContainerSettings(ModConfig config, String category, int defaultSlotX, int defaultSlotY, boolean isEnabledDefault) {
        this.category = category;

        CommentedConfig configData = config.getConfigData();
        slotX = configData.getIntOrElse(category + ".slotX", defaultSlotX);
        slotY = configData.getIntOrElse(category + ".slotY", defaultSlotY);
        anchorX = configData.getOrElse(category + ".anchorX", 0.5f);
        anchorY = configData.getOrElse(category + ".anchorY", 0.5f);
        isEnabled = configData.getOrElse(category + ".enabled", isEnabledDefault);
    }

    public void save(ModConfig config) {
        CommentedConfig configData = config.getConfigData();
        setWithComment(configData, category + ".slotX", slotX, "True if this screen should have a trash slot.");
        setWithComment(configData, category + ".slotY", slotY, "The x coordinate of the slot relative to the anchor.");
        setWithComment(configData, category + ".anchorX", (double) anchorX, "The y coordinate of the slot relative to the anchor.");
        setWithComment(configData, category + ".anchorY", (double) anchorY, "The x coordinate of the anchor point for the slot position.");
        setWithComment(configData, category + ".enabled", isEnabled, "The y coordinate of the anchor point for the slot position.");
        config.save();
    }

    private void setWithComment(CommentedConfig configData, String key, Object value, String comment) {
        configData.set(key, value);
        configData.setComment(key, comment);
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public int getSlotX() {
        return slotX;
    }

    public void setSlotX(int slotX) {
        this.slotX = slotX;
    }

    public int getSlotY() {
        return slotY;
    }

    public void setSlotY(int slotY) {
        this.slotY = slotY;
    }

    public float getAnchorX() {
        return anchorX;
    }

    public void setAnchorX(float anchorX) {
        this.anchorX = anchorX;
    }

    public float getAnchorY() {
        return anchorY;
    }

    public void setAnchorY(float anchorY) {
        this.anchorY = anchorY;
    }
}
