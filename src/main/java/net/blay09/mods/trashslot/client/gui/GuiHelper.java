package net.blay09.mods.trashslot.client.gui;

import net.minecraft.client.gui.Gui;

public class GuiHelper extends Gui {
	public static final GuiHelper instance = new GuiHelper();

	public static void drawGradientRect(int left, int top, int right, int bottom, float zLevel, int startColor, int endColor) {
		float oldZLevel = instance.zLevel;
		instance.zLevel = zLevel;
		instance.drawGradientRect(left, top, right, bottom, startColor, endColor);
		instance.zLevel = oldZLevel;
	}

}
