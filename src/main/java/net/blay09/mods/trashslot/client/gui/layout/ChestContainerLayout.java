package net.blay09.mods.trashslot.client.gui.layout;

import net.blay09.mods.trashslot.api.SlotRenderStyle;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.ChestContainer;

public class ChestContainerLayout extends SimpleGuiContainerLayout {
	public ChestContainerLayout() {
		enableDefaultSnaps();
		enableDefaultCollision();
		setEnabledByDefault();
	}

	@Override
	public int getSlotOffsetY(ContainerScreen<?> gui, SlotRenderStyle renderStyle) {
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

	@Override
	public String getContainerId(ContainerScreen<?> gui) {
		if(((ChestContainer) gui.getContainer()).inventorySlots.size() > 63) {
			return super.getContainerId(gui) + "_large";
		}
		return super.getContainerId(gui);
	}
}
