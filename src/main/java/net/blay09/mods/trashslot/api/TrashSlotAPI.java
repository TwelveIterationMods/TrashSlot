package net.blay09.mods.trashslot.api;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TrashSlotAPI {

	private static InternalMethods internalMethods;

	public static void __setupAPI(InternalMethods impl) {
		internalMethods = impl;
	}

	@SideOnly(Side.CLIENT)
	public static ISimpleGuiContainerLayout registerSimpleLayout(Class<? extends GuiContainer> clazz) {
		return internalMethods.registerSimpleLayout(clazz);
	}

	@SideOnly(Side.CLIENT)
	public static void registerLayout(Class<? extends GuiContainer> clazz, IGuiContainerLayout layout) {
		internalMethods.registerLayout(clazz, layout);
	}

}
