package net.blay09.mods.trashslot.client;

import com.google.common.collect.Maps;
import net.blay09.mods.trashslot.api.IGuiContainerLayout;
import net.blay09.mods.trashslot.client.gui.layout.SimpleGuiContainerLayout;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;

import java.util.Map;

public class LayoutManager {

    private static final Map<String, IGuiContainerLayout> layoutMap = Maps.newHashMap();

    public static IGuiContainerLayout getLayout(ContainerScreen<?> gui) {
        IGuiContainerLayout layout = layoutMap.get(gui.getClass().getName());
        if (layout == null) {
            return SimpleGuiContainerLayout.DEFAULT;
        }

        return layout;
    }

    public static void registerLayout(Class<? extends ContainerScreen<?>> clazz, IGuiContainerLayout layout) {
        layoutMap.put(clazz.getName(), layout);
    }

}
