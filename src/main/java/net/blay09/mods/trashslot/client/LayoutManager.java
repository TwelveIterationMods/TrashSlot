package net.blay09.mods.trashslot.client;

import com.google.common.collect.Maps;
import net.blay09.mods.trashslot.api.IGuiContainerLayout;
import net.blay09.mods.trashslot.client.gui.layout.SimpleGuiContainerLayout;
import net.minecraft.client.gui.inventory.GuiContainer;

import java.util.Map;

public class LayoutManager {

    private static final Map<Class<? extends GuiContainer>, IGuiContainerLayout> layoutMap = Maps.newHashMap();

    public static IGuiContainerLayout getLayout(GuiContainer gui) {
        IGuiContainerLayout layout = layoutMap.get(gui.getClass());
        if (layout == null) {
            return SimpleGuiContainerLayout.DEFAULT;
        }

        return layout;
    }

    public static void registerLayout(Class<? extends GuiContainer> clazz, IGuiContainerLayout layout) {
        layoutMap.put(clazz, layout);
    }

}
