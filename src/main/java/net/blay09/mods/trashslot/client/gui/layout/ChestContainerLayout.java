package net.blay09.mods.trashslot.client.gui.layout;

import net.blay09.mods.trashslot.api.SlotRenderStyle;
import net.minecraft.client.gui.inventory.GuiContainer;

public class ChestContainerLayout extends SimpleGuiContainerLayout {
	public ChestContainerLayout() {
		enableDefaultSnaps();
		enableDefaultCollision();
		setEnabledByDefault();
	}

	@Override
	public int getSlotOffsetY(GuiContainer gui, SlotRenderStyle renderStyle) {
		switch(renderStyle) {
			case ATTACH_BOTTOM_CENTER:
			case ATTACH_BOTTOM_LEFT:
			case ATTACH_BOTTOM_RIGHT:
			case ATTACH_LEFT_BOTTOM:
			case ATTACH_RIGHT_BOTTOM:
				return -1;
		}
		return 0;
	}
}
