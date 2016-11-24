package net.blay09.mods.trashslot.client;

import net.minecraftforge.common.config.Configuration;

public class TrashContainerSettings {

	public static final TrashContainerSettings NONE = new TrashContainerSettings() {
		@Override
		public void setEnabled(boolean isEnabled) {}
	};

	public static final String ENABLED_DESC = "True if this screen should have a trash slot.";
	public static final String SLOT_X_DESC = "The x coordinate of the slot relative to the anchor.";
	public static final String SLOT_Y_DESC = "The y coordinate of the slot relative to the anchor.";
	public static final String ANCHOR_X_DESC = "The x coordinate of the anchor point for the slot position.";
	public static final float ANCHOR_X_DEFAULT = 0.5f;
	public static final String ANCHOR_Y_DESC = "The y coordinate of the anchor point for the slot position.";
	public static final float ANCHOR_Y_DEFAULT = 0.5f;

	private final String category;
	private final int defaultSlotX;
	private final int defaultSlotY;
	private final boolean isEnabledDefault;

	public int slotX;
	public int slotY;
	public float anchorX;
	public float anchorY;
	private boolean isEnabled;

	private TrashContainerSettings() {
		category = "null";
		defaultSlotX = 0;
		defaultSlotY = 0;
		isEnabledDefault = false;
	}

	public TrashContainerSettings(Configuration config, String category, int defaultSlotX, int defaultSlotY, boolean isEnabledDefault) {
		this.category = category;
		this.defaultSlotX = defaultSlotX;
		this.defaultSlotY = defaultSlotY;
		this.isEnabledDefault = isEnabledDefault;
		slotX = config.getInt("Slot X", category, defaultSlotX, -600, 600, SLOT_X_DESC);
		slotY = config.getInt("Slot Y", category, defaultSlotY, -600, 600, SLOT_Y_DESC);
		anchorX = config.getFloat("Anchor X", category, ANCHOR_X_DEFAULT, 0f, 1f, ANCHOR_X_DESC);
		anchorY = config.getFloat("Anchor Y", category, ANCHOR_Y_DEFAULT, 0f, 1f, ANCHOR_Y_DESC);
		isEnabled = config.getBoolean("Enabled", category, isEnabledDefault, ENABLED_DESC);
	}

	public void save(Configuration config) {
		config.get(category, "Slot X", defaultSlotX, SLOT_X_DESC, -600, 600).set(slotX);
		config.get(category, "Slot Y", defaultSlotY, SLOT_Y_DESC, -600, 600).set(slotY);
		config.get(category, "Anchor X", 0.5f, ANCHOR_X_DESC).set(anchorX);
		config.get(category, "Anchor Y", 0.5f, ANCHOR_Y_DESC).set(anchorY);
		config.get(category, "Enabled", isEnabledDefault, ENABLED_DESC).set(isEnabled);
		if(config.hasChanged()) {
			config.save();
		}
	}

	public boolean isEnabled() {
		return isEnabled;
	}

	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

}
