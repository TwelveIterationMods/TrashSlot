package net.blay09.mods.trashslot.client.gui;

import com.google.common.collect.Lists;
import net.blay09.mods.trashslot.TrashSlot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.List;
import java.util.Set;

public class GuiFactory implements IModGuiFactory {
	@Override
	public void initialize(Minecraft minecraftInstance) {
	}

	@Override
	public Class<? extends GuiScreen> mainConfigGuiClass() {
		return ConfigGUI.class;
	}

	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
		return null;
	}

	@Override
	public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) {
		return null;
	}

	public static class ConfigGUI extends GuiConfig {
		public ConfigGUI(GuiScreen parentScreen) {
			super(parentScreen, getElements(), TrashSlot.MOD_ID, "", false, false, "TrashSlot");
		}

		private static List<IConfigElement> getElements() {
			List<IConfigElement> list = Lists.newArrayList();
			list.addAll(new ConfigElement(TrashSlot.config.getCategory("general")).getChildElements());
			return list;
		}
	}
}
