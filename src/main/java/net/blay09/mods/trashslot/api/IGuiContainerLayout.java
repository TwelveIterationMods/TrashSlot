package net.blay09.mods.trashslot.api;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.Rectangle;
import java.util.List;

@SideOnly(Side.CLIENT)
public interface IGuiContainerLayout {
	List<Rectangle> getCollisionAreas(GuiContainer gui);
	List<Snap> getSnaps(GuiContainer gui, SlotRenderStyle renderStyle);
	SlotRenderStyle getSlotRenderStyle(GuiContainer gui, int slotX, int slotY);
	int getDefaultSlotX(GuiContainer gui);
	int getDefaultSlotY(GuiContainer gui);
	boolean isEnabledByDefault();
	int getSlotOffsetX(GuiContainer gui, SlotRenderStyle renderStyle);
	int getSlotOffsetY(GuiContainer gui, SlotRenderStyle renderStyle);
	default String getContainerId(GuiContainer gui) {
		return gui.getClass().getName().replace('.', '/');
	}
}
