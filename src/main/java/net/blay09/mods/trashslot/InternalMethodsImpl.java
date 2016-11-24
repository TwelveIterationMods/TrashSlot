package net.blay09.mods.trashslot;

import net.blay09.mods.trashslot.api.IGuiContainerLayout;
import net.blay09.mods.trashslot.api.ISimpleGuiContainerLayout;
import net.blay09.mods.trashslot.api.InternalMethods;
import net.blay09.mods.trashslot.client.TrashClient;
import net.blay09.mods.trashslot.client.gui.layout.SimpleGuiContainerLayout;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class InternalMethodsImpl implements InternalMethods {

	@Override
	@SideOnly(Side.CLIENT)
	public ISimpleGuiContainerLayout registerSimpleLayout(Class<? extends GuiContainer> clazz) {
		SimpleGuiContainerLayout layout = new SimpleGuiContainerLayout();
		registerLayout(clazz, layout);
		return layout;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerLayout(Class<? extends GuiContainer> clazz, IGuiContainerLayout layout) {
		TrashClient.registerLayout(clazz, layout);
	}
}
