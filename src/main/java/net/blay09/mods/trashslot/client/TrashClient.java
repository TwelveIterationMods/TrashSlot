package net.blay09.mods.trashslot.client;

import com.google.common.collect.Maps;
import net.blay09.mods.trashslot.TrashSlot;
import net.blay09.mods.trashslot.api.IGuiContainerLayout;
import net.blay09.mods.trashslot.client.gui.layout.SimpleGuiContainerLayout;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.ContainerPlayer;

import java.util.Map;

public class TrashClient {

	private static final Map<String, TrashContainerSettings> settingsMap = Maps.newHashMap();
	private static final Map<Class<? extends GuiContainer>, IGuiContainerLayout> layoutMap = Maps.newHashMap();

	public static TrashContainerSettings getSettings(GuiContainer gui, IGuiContainerLayout layout) {
		String category = getConfigCategory(gui.inventorySlots);
		TrashContainerSettings settings = settingsMap.get(category);
		if(settings == null) {
			settings = new TrashContainerSettings(TrashSlot.config, category, layout.getDefaultSlotX(gui), layout.getDefaultSlotY(gui), layout.isEnabledByDefault());
			settingsMap.put(category, settings);
		}
		return settings;
	}

	public static IGuiContainerLayout getLayout(GuiContainer gui) {
		IGuiContainerLayout layout = layoutMap.get(gui.getClass());
		if(layout == null) {
			return SimpleGuiContainerLayout.DEFAULT;
		}
		return layout;
	}

	public static void registerLayout(Class<? extends GuiContainer> clazz, IGuiContainerLayout layout) {
		layoutMap.put(clazz, layout);
	}

	public static String getConfigCategory(Container container) {
		return "gui." + container.getClass().getName().replace('.', '/');
	}

}
