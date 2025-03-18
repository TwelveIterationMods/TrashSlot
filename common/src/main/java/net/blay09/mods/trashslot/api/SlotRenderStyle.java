package net.blay09.mods.trashslot.api;

public enum SlotRenderStyle {
	LONE(28, 28, 6, 6, 0, 0),
	ATTACH_TOP_CENTER(32, 25, 8, 13, 0, 7),
	ATTACH_TOP_LEFT(ATTACH_TOP_CENTER),
	ATTACH_TOP_RIGHT(32, 25, 4, 13, -4, 7),

	ATTACH_BOTTOM_CENTER(32, 25, 8, -1, 0, -4),
	ATTACH_BOTTOM_LEFT(ATTACH_BOTTOM_CENTER),
	ATTACH_BOTTOM_RIGHT(32, 25, 4, -1, -4, -4),

	ATTACH_LEFT_CENTER(25, 31, 13, 7, 7, 0),
	ATTACH_LEFT_TOP(ATTACH_LEFT_CENTER),
	ATTACH_LEFT_BOTTOM(25, 31, 13, 4, 7, -3),

	ATTACH_RIGHT_CENTER(25, 31, -1, 7, -4, 0),
	ATTACH_RIGHT_TOP(ATTACH_RIGHT_CENTER),
	ATTACH_RIGHT_BOTTOM(25, 31, -1, 4, -4, -3);

	private final int renderWidth;
	private final int renderHeight;
	private final int slotOffsetX;
	private final int slotOffsetY;
	private final int renderOffsetX;
	private final int renderOffsetY;

	SlotRenderStyle(int renderWidth, int renderHeight, int slotOffsetX, int slotOffsetY, int renderOffsetX, int renderOffsetY) {
		this.renderWidth = renderWidth;
		this.renderHeight = renderHeight;
		this.slotOffsetX = slotOffsetX;
		this.slotOffsetY = slotOffsetY;
		this.renderOffsetX = renderOffsetX;
		this.renderOffsetY = renderOffsetY;
	}

	SlotRenderStyle(SlotRenderStyle parent) {
		this.renderWidth = parent.renderWidth;
		this.renderHeight = parent.renderHeight;
		this.slotOffsetX = parent.slotOffsetX;
		this.slotOffsetY = parent.slotOffsetY;
		this.renderOffsetX = parent.renderOffsetX;
		this.renderOffsetY = parent.renderOffsetY;
	}

	public int getWidth() {
		return LONE.renderWidth;
	}

	public int getHeight() {
		return LONE.renderHeight;
	}

	public int getRenderWidth() {
		return renderWidth;
	}

	public int getRenderHeight() {
		return renderHeight;
	}

	public int getSlotOffsetX() {
		return slotOffsetX;
	}

	public int getSlotOffsetY() {
		return slotOffsetY;
	}

	public int getRenderOffsetX() {
		return renderOffsetX;
	}

	public int getRenderOffsetY() {
		return renderOffsetY;
	}
}
