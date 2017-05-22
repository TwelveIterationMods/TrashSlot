package net.blay09.mods.trashslot.client;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.blay09.mods.trashslot.TrashSlot;
import net.blay09.mods.trashslot.api.IGuiContainerLayout;
import net.blay09.mods.trashslot.client.gui.layout.SimpleGuiContainerLayout;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.ContainerPlayer;

import java.util.Map;
import java.util.Set;

public class TrashClient {

	private static final Map<String, TrashContainerSettings> settingsMap = Maps.newHashMap();
	private static final Map<Class<? extends GuiContainer>, IGuiContainerLayout> layoutMap = Maps.newHashMap();

	private static final Set<String> blacklist = Sets.newHashSet();

	static {
		blacklist.add("gui.slimeknights/tconstruct/tools/common/client/module/GuiTinkerTabs");
		blacklist.add("gui.slimeknights/tconstruct/tools/common/client/GuiCraftingStation");
		blacklist.add("gui.slimeknights/tconstruct/tools/common/client/GuiPatternChest");
		blacklist.add("gui.slimeknights/tconstruct/tools/common/client/module/GuiButtonsStencilTable");
		blacklist.add("gui.slimeknights/tconstruct/tools/common/client/GuiPartBuilder");
	}

	public static TrashContainerSettings getSettings(GuiContainer gui, IGuiContainerLayout layout) {
		String category = getConfigCategory(gui);
		if(blacklist.contains(category)) {
			return TrashContainerSettings.NONE;
		}
		return settingsMap.computeIfAbsent(category, c -> new TrashContainerSettings(TrashSlot.config, c, layout.getDefaultSlotX(gui), layout.getDefaultSlotY(gui), layout.isEnabledByDefault()));
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

	public static String getConfigCategory(GuiContainer gui) {
		IGuiContainerLayout guiContainerLayout = getLayout(gui);
		return "gui." + guiContainerLayout.getContainerId(gui);
	}

}
