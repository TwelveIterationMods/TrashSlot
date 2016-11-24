package net.blay09.mods.trashslot.api;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface InternalMethods {
	@SideOnly(Side.CLIENT)
	ISimpleGuiContainerLayout registerSimpleLayout(Class<? extends GuiContainer> clazz);
	@SideOnly(Side.CLIENT)
	void registerLayout(Class<? extends GuiContainer> clazz, IGuiContainerLayout layout);
}
